package org.rubilnik.auth_service.http_controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.rubilnik.auth_service.App;
import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.EmailService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

// Using ResponceEntity at top level of controller mapping, using throw ResponceStatusException in submethods (not bothering with return), ResponceStatusException handled by controller
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class HTTP_User_Controller {

    @Autowired
    UserMemoService memo;
    @Autowired
    EmailService emailService;
    @Autowired
    Environment env;

//    public static class UserValidationInfo{
//        public String id,password,email;
//    }

    static class PostUserJsonBody{
        public User user;
    }
    @PostMapping()
    ResponseEntity<String> postUser(@RequestBody PostUserJsonBody body) throws IOException {
        if ( !env.matchesProfiles("server") ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only for central server profile");

        App.logObjectAsJson(body);
        var info = new Records.UserValidationInfo(body.user.getId(),body.user.getEmail(),body.user.getPassword());
        var userFromDB = memo.get(body.user.getId(),body.user.getEmail());
        if (userFromDB.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with such id already exists");

        emailService.sendVerifyEmail(info, body.user.getName());

        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Verify your email. Check your email for the verification link");
    }

    static class DeleteUserJsonBody{
        public Records.UserValidationInfo validation;
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
        public Records.UserValidationInfo validation;
        public User user;
    }
    @PutMapping()
    ResponseEntity<?> putUser(@RequestBody PutUserJsonBody body){
        App.logObjectAsJson(body);
        var user = memo.getValid(body.validation);
        user.setName(body.user.getName());
//        user.setEmail(body.user.getEmail());
        user.setPassword(body.user.getPassword());
        memo.save(user);
        return ResponseEntity.ok().build();
    }

    static class PostUserVerificationJsonBody{
        public Records.UserValidationInfo validation;
    }
    @PostMapping("/verify")
    ResponseEntity<?> postUserVerification(@RequestBody PostUserVerificationJsonBody body){
        App.logObjectAsJson(body);
        memo.getValid(body.validation);
        return ResponseEntity.ok().build();
    }

    static class PostUserGetJsonBody{
        public Records.UserValidationInfo validation;
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