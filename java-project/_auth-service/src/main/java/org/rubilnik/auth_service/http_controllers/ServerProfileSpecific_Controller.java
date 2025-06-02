package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.UserRegisterEmailVerificationTokenService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@EnableAspectJAutoProxy
@Controller
//@CrossOrigin("*")
@Profile("server")
public class ServerProfileSpecific_Controller {
    @Autowired
    UserRegisterEmailVerificationTokenService userRegisterEmailVerificationTokenService;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;
    @Autowired
    UserMemoService memo;

    @GetMapping("/verify")
    String verifyUserToken(Model model, @RequestParam("email") String email, @RequestParam("token") String token){
        var savedToken = userRegisterEmailVerificationTokenService.getToken(email);
        if (savedToken.isEmpty()) { model.addAttribute("message", "Token verification failed, no token found for email: "+email); return "message";}
        if (!savedToken.get().equals(token)) { model.addAttribute("message", "Token verification failed, wrong token provided for email: "+email); return "message";}
        
        var info = userRegisterEmailVerificationTokenService.getUserTokenInfo(email);
        userRegisterEmailVerificationTokenService.deleteToken(email);
        var user = new User(info.name(), info.email(), info.password());
        memo.save(user);
        user = memo.getValid(new Records.UserValidationInfo(info.id(), info.email(), info.password()));

        model.addAttribute("message", "token verified, user created, continue to "+centralServerUrl+"/login");
        model.addAttribute("link", centralServerUrl+"/login");

        return "message";
    }
}