package org.rubilnik.auth_service.services.userMemo;

import java.util.Optional;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.repositories.UserRepository;
import org.rubilnik.auth_service.services.UserHttpSessionTokenManager;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("server")
public class UserMemoService_InDB implements UserMemoService {
    @Autowired
    UserRepository dbRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserHttpSessionTokenManager tokenManager;

    @Override
    public void save(User user) {
        user.setPassword( passwordEncoder.encode(user.getPassword()) );
        dbRepository.save(user);
    }
    @Override
    public Optional<User> get(String id, String email) throws ResponseStatusException {
        Optional<User> opt;
        if (id!=null) opt = dbRepository.findById(id);
        else if (email!=null) opt = dbRepository.findByEmail(email);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must contain User id or email");
        return opt;
    }
    @Override
    public User getValid(Records.UserValidationInfo info) throws ResponseStatusException {
        var opt = get(info.id(), info.email());
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such id wasn't found");
        var user = opt.get();
        if ( !passwordEncoder.matches(info.password(),user.getPassword()) ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user password");
        return user;
    }
    @Override
    public Iterable<User> getAll() {
        return dbRepository.findAll(); 
    }
    @Override
    public void delete(User user) {
        dbRepository.delete(user);
    }
    @Override
    public User getByName(String name) {
        return dbRepository.findByName(name).orElse(null);
    }
    @Override
    public User getValid(String token) throws ResponseStatusException {
        var id = tokenManager.getUserId(token).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid token provided."));
        return dbRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't find user associated with such token."));
    }
}
