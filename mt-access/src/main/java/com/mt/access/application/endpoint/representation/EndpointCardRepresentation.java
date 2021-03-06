package com.mt.access.application.endpoint.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EndpointCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String resourceId;
    private String resourceName;
    private String path;
    private String method;
    private Integer version;
    private boolean websocket;
    private boolean csrfEnabled;
    private boolean secured;
    private String corsProfileId;
    private String roleId;
    private String cacheProfileId;

    public EndpointCardRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.name = endpoint.getName();
        this.cacheProfileId = endpoint.getCacheProfileId() != null ? endpoint.getCacheProfileId().getDomainId() : null;
        this.websocket = endpoint.isWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
        this.roleId = endpoint.getPermissionId() != null ? endpoint.getPermissionId().getDomainId() : null;
        this.secured = endpoint.isSecured();
        this.csrfEnabled = endpoint.isCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId() != null ? endpoint.getCorsProfileId().getDomainId() : null;
    }

    public static void updateDetail(List<EndpointCardRepresentation> data) {
        Set<ClientId> collect = data.stream().map(e -> new ClientId(e.resourceId)).collect(Collectors.toSet());
        if(!collect.isEmpty()){
            Set<Client> allByIds = ApplicationServiceRegistry.getClientApplicationService().findAllByIds(collect);
            data.forEach(e -> allByIds.stream().filter(ee -> ee.getClientId().getDomainId().equals(e.resourceId)).findFirst().ifPresent(ee -> {
                e.resourceName = ee.getName();
            }));
        }
    }
}
