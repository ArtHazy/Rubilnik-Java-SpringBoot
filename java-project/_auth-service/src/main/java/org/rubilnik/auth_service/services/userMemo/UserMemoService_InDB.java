package org.rubilnik.auth_service.services.userMemo;

import java.util.Optional;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.repositories.UserRepository;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("server")
public class UserMemoService_InDB implements UserMemo {
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public void save(User user) {
        user.setPassword( encoder.encode(user.getPassword()) );
        repository.save(user);
    }
    @Override
    public Optional<User> get(String id, String email) throws ResponseStatusException {
        Optional<User> opt;
        if (id!=null) opt = repository.findById(id);
        else if (email!=null) opt = repository.findByEmail(email);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must contain User id or email");
        return opt;
    }
    @Override
    public User getValid(Records.UserValidationInfo info) throws ResponseStatusException {
        var opt = get(info.id(), info.email());
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such id wasn't found");
        var user = opt.get();
        if ( !encoder.matches(info.password(),user.getPassword()) ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user password");
        return user;
    }
    @Override
    public Iterable<User> getAll() {
        return repository.findAll();
    }
    @Override
    public void delete(User user) {
        repository.delete(user);
    }
    @Override
    public User getByName(String name) {
        return repository.findByName(name).orElse(null);
    }
}
