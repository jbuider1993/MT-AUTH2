package com.mt.access.application.client.command;

import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Set;

@Data
@Slf4j
public class ClientCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
    private String projectId;
    private boolean hasSecret = false;
    private String description;
    private String name;
    private String path;

    private Set<GrantType> grantTypeEnums;
    private Set<ClientType> types;

    private int accessTokenValiditySeconds = 0;
    @Nullable
    private Set<String> registeredRedirectUri;
    private int refreshTokenValiditySeconds = 0;
    @Nullable
    private Set<String> resourceIds;

    private boolean resourceIndicator = false;

    private boolean autoApprove = false;
}
