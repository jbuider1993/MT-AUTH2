package com.mt.access.domain.model;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface CurrentUserService {

    Set<String> userPermissionIds();

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();

    UserId getUserId();

    ClientId getClientId();

    Set<ProjectId> getTenantIds();
    Set<PermissionId> getPermissionIds();
}
