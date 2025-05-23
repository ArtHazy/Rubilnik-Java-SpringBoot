package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.App;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Using ResponceEntity at top level of controller mapping, using throw ResponceStatusException in submethods (not bothering with return), ResponceStatusException handled by controller
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class HTTP_User_Controller {

    @Autowired
    UserMemoService memo;

    public static class UserValidationInfo{
        public String id,password,email;
    }

    static class PostUserJsonBody{
        public User user;
    }
    @PostMapping()
    ResponseEntity<?> postUser(@RequestBody PostUserJsonBody body){
        App.logObjectAsJson(body);
        var info = new UserValidationInfo();
        info.email = body.user.getEmail(); info.id = body.user.getId(); info.password = body.user.getPassword();
        var userFromDB = memo.get(body.user.getId(),body.user.getEmail());
        if (userFromDB.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with such id already exists");
        else {
            var user = new User(body.user.getName(),body.user.getEmail(),body.user.getPassword());
            memo.save(user);
            user = memo.getValid(info);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(user);
        }
    }

    static class DeleteUserJsonBody{
        public UserValidationInfo validation;
    }
    @DeleteMapping()
    ResponseEntity<?> deleteUser( /*Authentication auth,*/ @RequestBody DeleteUserJsonBody body){
        App.logObjectAsJson(body);
        var user = memo.getValid(body.validation);
        memo.delete(user);
        user.clearID();
        return ResponseEntity.ok().build();
    }

    static class PutUserJsonBody{
        public UserValidationInfo validation;
        public User user;
    }
    @PutMapping()
    ResponseEntity<?> putUser(@RequestBody PutUserJsonBody body){
        App.logObjectAsJson(body);
        var user = memo.getValid(body.validation);
        user.setName(body.user.getName());
        user.setEmail(body.user.getEmail());
        user.setPassword(body.user.getPassword());
        memo.save(user);
        return ResponseEntity.ok().build();
    }

    static class PostUserVerificationJsonBody{
        public UserValidationInfo validation;
    }
    @PostMapping("/verify")
    ResponseEntity<?> postUserVerification(@RequestBody PostUserVerificationJsonBody body){
        App.logObjectAsJson(body);
        memo.getValid(body.validation);
        return ResponseEntity.ok().build();
    }

    static class PostUserGetJsonBody{
        public UserValidationInfo validation;
    }
    @PostMapping("/get")
    ResponseEntity<?> postUserGet(
            @RequestBody PostUserGetJsonBody body
            //@RequestHeader("Authorization") String auth
    ){
        App.logObjectAsJson(body);
        var user = memo.getValid(body.validation);
        return ResponseEntity.ok().body(user);
    }
}