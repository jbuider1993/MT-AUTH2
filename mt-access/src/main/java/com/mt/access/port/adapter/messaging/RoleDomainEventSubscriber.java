package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.endpoint.event.EndpointShareRemoved;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.client.event.ClientCreated.CLIENT_CREATED;
import static com.mt.access.domain.model.endpoint.event.EndpointShareRemoved.ENDPOINT_SHARED_REMOVED;
import static com.mt.access.domain.model.permission.event.ProjectPermissionCreated.PROJECT_PERMISSION_CREATED;
@Slf4j
@Component
public class RoleDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, PROJECT_PERMISSION_CREATED, (event) -> {
            ProjectPermissionCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProjectPermissionCreated.class);
            ApplicationServiceRegistry.getRoleApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CLIENT_CREATED, (event) -> {
            ClientCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ClientCreated.class);
            ApplicationServiceRegistry.getRoleApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, ENDPOINT_SHARED_REMOVED, (event) -> {
            EndpointShareRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), EndpointShareRemoved.class);
            ApplicationServiceRegistry.getRoleApplicationService().handle(deserialize);
        });
    }
}
