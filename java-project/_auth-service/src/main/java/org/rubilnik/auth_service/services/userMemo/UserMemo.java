package org.rubilnik.auth_service.services.userMemo;

import java.util.Optional;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.core.users.User;
import org.springframework.web.server.ResponseStatusException;

public interface UserMemo {
    void save(User user);
    Optional<User> get(String id, String email) throws ResponseStatusException;
    User getValid(Records.UserValidationInfo info) throws ResponseStatusException;
    Iterable<User> getAll();
    void delete(User user);
    User getByName(String name);
}
