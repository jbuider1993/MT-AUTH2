package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProxyCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointRepresentation;
import com.mt.access.application.endpoint.representation.EndpointSharedCardRepresentation;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.access.infrastructure.Utility.updateProjectId;
import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json")
public class EndpointResource {

    @PostMapping(path = "projects/{projectId}/endpoints")
    public ResponseEntity<Void> createForRoot(@PathVariable String projectId, @RequestBody EndpointCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getEndpointApplicationService().create(projectId, command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/endpoints")
    public ResponseEntity<SumPagedRep<?>> readForRootByQuery(@PathVariable String projectId,
                                                             @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                                             @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                             @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                             @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = updateProjectId(queryParam, projectId);
        SumPagedRep<Endpoint> endpoints = ApplicationServiceRegistry.getEndpointApplicationService().tenantQuery(queryParam, pageParam, config);
        SumPagedRep<EndpointCardRepresentation> rep = new SumPagedRep<>(endpoints, EndpointCardRepresentation::new);
        EndpointCardRepresentation.updateDetail(rep.getData());
        return ResponseEntity.ok(rep);
    }

    @GetMapping(path = "mngmt/endpoints")
    public ResponseEntity<SumPagedRep<?>> readForRootByQuery2(
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        SumPagedRep<Endpoint> endpoints = ApplicationServiceRegistry.getEndpointApplicationService().adminQuery(queryParam, pageParam, config);
        SumPagedRep<EndpointCardRepresentation> endpointCardRepresentationSumPagedRep = new SumPagedRep<>(endpoints, EndpointCardRepresentation::new);
        EndpointCardRepresentation.updateDetail(endpointCardRepresentationSumPagedRep.getData());
        return ResponseEntity.ok(endpointCardRepresentationSumPagedRep);
    }

    @GetMapping(path = "mngmt/endpoints/{id}")
    public ResponseEntity<EndpointRepresentation> readForRootByQuery3(
            @PathVariable String id,
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Endpoint> endpoint = ApplicationServiceRegistry.getEndpointApplicationService().adminEndpoint(id);
        return endpoint.map(value -> ResponseEntity.ok(new EndpointRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping("endpoints/proxy")
    public ResponseEntity<SumPagedRep<?>> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        SumPagedRep<EndpointProxyCardRepresentation> endpoints = ApplicationServiceRegistry.getEndpointApplicationService().internalQuery(queryParam, pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<EndpointRepresentation> readForRootById(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String projectId, @PathVariable String id) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Endpoint> endpoint = ApplicationServiceRegistry.getEndpointApplicationService().tenantEndpoint(projectId, id);
        return endpoint.map(value -> ResponseEntity.ok(new EndpointRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<Void> replaceForRootById(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String projectId, @RequestBody EndpointUpdateCommand command,
                                                   @PathVariable String id,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getEndpointApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<Void> deleteForRootById(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getEndpointApplicationService().removeEndpoint(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/endpoints")
    public ResponseEntity<Void> deleteForAdminByQuery(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String projectId, @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                      @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getEndpointApplicationService().removeEndpoints(projectId, queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/endpoints/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String projectId, @PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch patch,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getEndpointApplicationService().patchEndpoint(projectId, id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "mngmt/endpoints/event/reload")
    public ResponseEntity<Void> postForRoot(@RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        ApplicationServiceRegistry.getEndpointApplicationService().reloadEndpointCache(changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "endpoints/shared")
    public ResponseEntity<SumPagedRep<EndpointSharedCardRepresentation>> getSharedApis(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                       @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                       @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        SumPagedRep<Endpoint> shared = ApplicationServiceRegistry.getEndpointApplicationService().getShared(queryParam, pageParam, config);
        SumPagedRep<EndpointSharedCardRepresentation> rep = new SumPagedRep<>(shared, EndpointSharedCardRepresentation::new);
        EndpointSharedCardRepresentation.updateDetail(rep.getData());
        return ResponseEntity.ok(rep);
    }
}
