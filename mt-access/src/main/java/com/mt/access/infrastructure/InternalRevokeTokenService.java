package com.mt.access.infrastructure;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.revoke_token.RevokeTokenCreateCommand;
import com.mt.access.domain.model.revoke_token.RevokeTokenService;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InternalRevokeTokenService implements RevokeTokenService {

    @Override
    public void revokeToken(DomainId domainId) {
        RevokeTokenCreateCommand createRevokeTokenCommand = new RevokeTokenCreateCommand(domainId.getDomainId());
        ApplicationServiceRegistry.getRevokeTokenApplicationService().internalOnlyCreate(createRevokeTokenCommand, UUID.randomUUID().toString());
        log.debug("complete revoke token");
    }

}
