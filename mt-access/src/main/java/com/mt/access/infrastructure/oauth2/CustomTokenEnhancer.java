package com.mt.access.infrastructure.oauth2;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.domainId.DomainId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * capture issued at time to enable token revocation feature,
 * use user id instead of username to enhance security
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    private static final String NOT_USED = "not_used";
    @Value("${mt.project.id}")
    private String authProjectId;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> info = new HashMap<>();
        info.put("iat", Instant.now().getEpochSecond());
        if (!authentication.isClientOnly()) {
            UserId userId = new UserId(authentication.getName());
            info.put("uid", userId.getDomainId());
            //for user
            Set<String> scope = authentication.getOAuth2Request().getScope();
            if (scope != null && scope.size() > 0 && !NOT_USED.equals(scope.stream().findFirst().get())) {
                //only one projectId allowed
                //get tenant project permission
                Optional<String> first = scope.stream().findFirst();
                Optional<UserRelation> userRelation = ApplicationServiceRegistry.getUserRelationApplicationService().getUserRelation(userId, new ProjectId(first.get()));
                userRelation.ifPresent(e -> {
                    Set<PermissionId> compute = DomainRegistry.getComputePermissionService().compute(e);
                    info.put("permissionIds", compute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                    info.put("projectId", e.getProjectId().getDomainId());
                });
            } else {
                //get auth project permission and user tenant projects
                Optional<UserRelation> userRelation = ApplicationServiceRegistry.getUserRelationApplicationService().getUserRelation(userId, new ProjectId(authProjectId));
                userRelation.ifPresent(relation -> {
                    Set<PermissionId> compute = DomainRegistry.getComputePermissionService().compute(relation);
                    info.put("permissionIds", compute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                    info.put("projectId", relation.getProjectId().getDomainId());
                    info.put("tenantId", relation.getTenantIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                });
            }
        } else {
            //for client
            ClientId clientId = new ClientId(authentication.getName());
            Optional<Client> client = ApplicationServiceRegistry.getClientApplicationService().clientOfId(clientId);
            client.ifPresent(client1->{
                RoleId roleId = client1.getRoleId();
                Optional<Role> byId = ApplicationServiceRegistry.getRoleApplicationService().getById(roleId);
                byId.ifPresent(role->{
                    info.put("projectId", client1.getProjectId().getDomainId());
                    info.put("permissionIds", role.getPermissionIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                });
            });
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
