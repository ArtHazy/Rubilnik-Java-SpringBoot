package org.rubilnik.auth_service.repositories;

import java.util.Optional;

import org.rubilnik.core.users.User;
import org.springframework.data.repository.CrudRepository;

// spring creates an object of this interface and adds it to the @SpringBootApplication
public interface UserRepository extends CrudRepository<User,String> {
    // default methods

    // custom method. Spring converts to sql query automatically. Same as with annotation below:
    //  @Query(value = "SELECT * FROM users WHERE email = :email, nativeQuery = true)
    Optional<User> findByEmail(String email);
}
