package org.rubilnik.auth_service.services.userMemo;

import java.util.Optional;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.core.users.User;
import org.springframework.web.server.ResponseStatusException;

public interface UserMemoService {
    void save(User user);
    Optional<User> get(String id, String email) throws ResponseStatusException;
    User getValid(UserValidationInfo info) throws ResponseStatusException;
    Iterable<User> getAll();
    void delete(User user);
}
