package org.rubilnik.auth_service.http_controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.CurrentHttpSessionUserResolver;
import org.rubilnik.auth_service.services.EmailService;
import org.rubilnik.auth_service.services.EmailVerificationTokenService;
import org.rubilnik.auth_service.services.userMemo.UserMemo;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

// Using ResponceEntity at top level of controller mapping, using throw ResponceStatusException in submethods (not bothering with return), ResponceStatusException handled by controller
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/user")
//@CrossOrigin("*")
public class HttpUserController {
    @Autowired
    Environment env;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    UserMemo memo;
    @Autowired(required = false)
    EmailService emailService;
    @Autowired
    CurrentHttpSessionUserResolver clientResolver;
    @Autowired(required = false)
    EmailVerificationTokenService emailVerificationTokenService;

    record PostUserJsonBody(User user) {}
    @PostMapping()
    ResponseEntity<String> postUser(@RequestBody PostUserJsonBody body) throws MessagingException {
        if ( !env.matchesProfiles("server") )
            return ResponseEntity.badRequest().body("Only for central server profile");
        var user = body.user();
        var memoUser = memo.get(user.getId(),user.getEmail());
        if (memoUser.isPresent())
            return ResponseEntity.badRequest().body("User with such id already exists");
        var info = new Records.UserValidationInfo(user.getId(),user.getEmail(),user.getPassword());
        var token = emailVerificationTokenService.createAndStoreToken(user.getName(),info);
        emailService.sendVerifyEmail(user.getName(), info.email(), token);
        return ResponseEntity.ok("Verify your email. Check your email for the verification link");
    }

    // Instead of default .formLogin() in SecurityFilterChain
    record PostUserGetJsonBody(Records.UserValidationInfo validation){};
    @PostMapping("/login")
    ResponseEntity<?> postUserGet(
        @RequestBody PostUserGetJsonBody body,
        HttpServletRequest req, HttpServletResponse res
    ){
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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

    record PutUserJsonBody(User user){};
    @PutMapping()
    ResponseEntity<?> putUser(@RequestBody PutUserJsonBody body){
        var user = clientResolver.getCurrent();
        user.setName(body.user.getName());
//        user.setEmail(body.user.getEmail());
        user.setPassword(body.user.getPassword());
        memo.save(user);
        return ResponseEntity.ok().build();
    }
    @GetMapping()
    ResponseEntity<User> getUser(){
        var user = clientResolver.getCurrent();
        return ResponseEntity.ok(user);
    }

//    @PostMapping("/verify")
//    ResponseEntity<?> postUserVerification(){
//        clientResolver.getCurrent();
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/validate")
    public ResponseEntity<User> validateToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        var user = clientResolver.getCurrent(); // resolve user from authentication
        return ResponseEntity.ok(user);
    }
}