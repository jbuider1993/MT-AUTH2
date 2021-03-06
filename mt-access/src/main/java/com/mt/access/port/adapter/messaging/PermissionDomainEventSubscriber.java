package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.event.EndpointShareAdded;
import com.mt.access.domain.model.endpoint.event.EndpointShareRemoved;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.endpoint.event.EndpointShareAdded.ENDPOINT_SHARED_ADDED;
import static com.mt.access.domain.model.endpoint.event.EndpointShareRemoved.ENDPOINT_SHARED_REMOVED;
import static com.mt.access.domain.model.endpoint.event.SecureEndpointCreated.SECURE_ENDPOINT_CREATED;
import static com.mt.access.domain.model.project.event.ProjectCreated.PROJECT_CREATED;

@Slf4j
@Component
public class PermissionDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, PROJECT_CREATED, (event) -> {
            ProjectCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProjectCreated.class);
            ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, SECURE_ENDPOINT_CREATED, (event) -> {
            SecureEndpointCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SecureEndpointCreated.class);
            ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, ENDPOINT_SHARED_ADDED, (event) -> {
            EndpointShareAdded deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), EndpointShareAdded.class);
            ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, ENDPOINT_SHARED_REMOVED + "_permission_handler", (event) -> {
            EndpointShareRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), EndpointShareRemoved.class);
            ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
        }, ENDPOINT_SHARED_REMOVED);
    }
}
