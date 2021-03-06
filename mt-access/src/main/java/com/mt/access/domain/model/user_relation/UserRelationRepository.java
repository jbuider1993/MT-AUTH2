package com.mt.access.domain.model.user_relation;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface UserRelationRepository {
    void add(UserRelation userRelation);

    SumPagedRep<UserRelation> getByQuery(UserRelationQuery query);

    void remove(UserRelation e);

    SumPagedRep<UserRelation> getByUserId(UserId id);

    Optional<UserRelation> getByUserIdAndProjectId(UserId id, ProjectId projectId);
}
