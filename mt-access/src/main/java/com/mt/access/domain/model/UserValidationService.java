package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserValidationService {
    public void validate(User user, ValidationNotificationHandler handler) {
        Optional<PendingUser> pendingUser = DomainRegistry.getPendingUserRepository().pendingUserOfEmail(new RegistrationEmail(user.getEmail().getEmail()));
        if (pendingUser.isEmpty())
            handler.handleError("please get activation code first");
        Optional<User> user1 = DomainRegistry.getUserRepository().searchExistingUserWith(user.getEmail());
        if (user1.isPresent())
            handler.handleError("already an user " + user.getEmail().getEmail());
    }
}
