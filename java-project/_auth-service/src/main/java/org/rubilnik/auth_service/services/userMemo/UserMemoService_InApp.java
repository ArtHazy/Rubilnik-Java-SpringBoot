package org.rubilnik.auth_service.services.userMemo;

import java.util.List;
import java.util.Optional;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("desktop")
public class UserMemoService_InApp implements UserMemoService {
    @Autowired
    PasswordEncoder passwordEncoder;
    User onlyValid = new User("admin", "admin", "admin" /*passwordEncoder.encode("admin")*/);
    @Override
    public void save(User user) {
        if (!user.getId().equals(onlyValid.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only one user allowed");
//        user.setPassword( passwordEncoder.encode(user.getPassword()) );
        onlyValid = user;
    }
    @Override
    public Optional<User> get(String id, String email) {
        if ( id!=null && id.equals(onlyValid.getId()) ) return Optional.of(onlyValid); 
        if ( email!=null && email.equals(onlyValid.getEmail())) return Optional.of(onlyValid);
        return Optional.empty();
    }
    @Override
    public User getValid(Records.UserValidationInfo info) throws ResponseStatusException {
        var opt = get(info.id(), info.email());
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No user with such ID's was found");
        var user = opt.get();
        if ( !(info.email().equals(onlyValid.getEmail()) || info.id().equals(onlyValid.getId())) ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user email");
        if (!info.password().equals(user.getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");

//        if ( !passwordEncoder.matches(info.password,user.getPassword()) ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");

        // if (!info.password.equals(onlyValid.getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");
        return user;
    }
    @Override
    public Iterable<User> getAll() {
        return List.of(onlyValid);
    }
    @Override
    public void delete(User user) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete an only user");
    }
    @Override
    public User getByName(String name) {
        return onlyValid;
    }
}