package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.*;
import com.mt.access.application.user.representation.UserAdminRepresentation;
import com.mt.access.application.user.representation.UserCardRepresentation;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.application.user.representation.UserTenantRepresentation;
import com.mt.access.application.user_relation.UpdateUserRelationCommand;
import com.mt.access.domain.model.user.User;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json")
public class UserResource {


    @PostMapping(path = "users")
    public ResponseEntity<Void> createForApp(@RequestBody UserCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getUserApplicationService().create(command, changeId)).build();
    }

    @GetMapping(path = "mngmt/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                   @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                   @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserApplicationService().users(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }


    @GetMapping("mngmt/users/{id}")
    public ResponseEntity<UserAdminRepresentation> readForAdminById(@PathVariable String id) {
        Optional<User> user = ApplicationServiceRegistry.getUserApplicationService().user(id);
        return user.map(value -> ResponseEntity.ok(new UserAdminRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @PutMapping("mngmt/users/{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody UpdateUserCommand command,
                                               @PathVariable String id,
                                               @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("mngmt/users/{id}")
    public ResponseEntity<Void> deleteForAdminById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().delete(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "mngmt/users/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForAdminById(@PathVariable(name = "id") String id,
                                                  @RequestBody JsonPatch command,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "mngmt/users")
    public ResponseEntity<Void> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "users/pwd")
    public ResponseEntity<Void> updateForUser(@RequestBody UserUpdateBizUserPasswordCommand command,
                                              @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().updatePassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "users/forgetPwd")
    public ResponseEntity<Void> forgetPwd(@RequestBody UserForgetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().forgetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "users/resetPwd")
    public ResponseEntity<Void> resetPwd(@RequestBody UserResetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().resetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> findUserForProject(
            @PathVariable String projectId,
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = Utility.updateProjectId(queryParam, projectId);
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserRelationApplicationService().tenantUsers(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }

    @GetMapping(path = "projects/{projectId}/users/{id}")
    public ResponseEntity<UserTenantRepresentation> findUserForProject2(
            @PathVariable String projectId,
            @PathVariable String id,
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<UserTenantRepresentation> user = ApplicationServiceRegistry.getUserRelationApplicationService().tenantUserDetail(projectId, id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping(path = "users/profile")
    public ResponseEntity<UserProfileRepresentation> findUserForProject3(
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<User> user = ApplicationServiceRegistry.getUserRelationApplicationService().myProfile();
        return user.map(value -> ResponseEntity.ok(new UserProfileRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @PutMapping(path = "projects/{projectId}/users/{id}")
    public ResponseEntity<UserTenantRepresentation> replaceUserDetailForProject(
            @PathVariable String projectId,
            @PathVariable String id,
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
            @RequestBody UpdateUserRelationCommand command
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService().update(projectId, id, command);
        return ResponseEntity.ok().build();
    }
}
