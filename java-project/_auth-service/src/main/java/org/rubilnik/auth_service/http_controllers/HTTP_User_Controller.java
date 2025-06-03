package org.rubilnik.auth_service.http_controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rubilnik.auth_service.App;
import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.CurrentHttpSessionUserResolver;
import org.rubilnik.auth_service.services.EmailService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

// Using ResponceEntity at top level of controller mapping, using throw ResponceStatusException in submethods (not bothering with return), ResponceStatusException handled by controller
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/user")
//@CrossOrigin("*")
public class HTTP_User_Controller {
    @Autowired
    Environment env;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    UserMemoService memo;
    @Autowired
    EmailService emailService;
    @Autowired
    CurrentHttpSessionUserResolver clientResolver;

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
    static class PostUserGetJsonBody{
        public Records.UserValidationInfo validation;
    }
    // Instead of default .formLogin() in SecurityFilterChain
    @PostMapping("/login")
    ResponseEntity<?> postUserGet(
        @RequestBody PostUserGetJsonBody body,
        HttpServletRequest req, HttpServletResponse res
    ){
        App.logObjectAsJson(body);
        var user = memo.getValid(body.validation);
        try {
            // BREAKABLE (403) Conflicts with .formLogin() in config
            var token = new UsernamePasswordAuthenticationToken( body.validation.email(), body.validation.password() );
            Authentication authentication = authManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Persist authentication into the session
            var session = req.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping()
    ResponseEntity<?> deleteUser(){
        var user = clientResolver.getCurrent();
        memo.delete(user);
        user.clearID();
        return ResponseEntity.ok().build();
    }

    static class PutUserJsonBody{
        public User user;
    }
    @PutMapping()
    ResponseEntity<?> putUser(@RequestBody PutUserJsonBody body){
        var user = clientResolver.getCurrent();
        user.setName(body.user.getName());
//        user.setEmail(body.user.getEmail());
        user.setPassword(body.user.getPassword());
        memo.save(user);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/hi")
    String hi(){
        return "hi";
    }

    @PostMapping("/verify")
    ResponseEntity<?> postUserVerification(){
        clientResolver.getCurrent();
        return ResponseEntity.ok().build();
    }
}