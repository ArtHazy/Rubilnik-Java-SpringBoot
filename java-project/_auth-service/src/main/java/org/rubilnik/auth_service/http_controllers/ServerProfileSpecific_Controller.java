package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.EmailVerificationTokenService;
import org.rubilnik.auth_service.services.userMemo.UserMemo;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@EnableAspectJAutoProxy
@Controller
//@CrossOrigin("*")
@Profile("server")
public class ServerProfileSpecific_Controller {
    @Autowired
    EmailVerificationTokenService emailVerificationTokenService;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;
    @Autowired
    UserMemo memo;

    @GetMapping("/verify")
    String verifyUserToken(Model model, @RequestParam("email") String email, @RequestParam("token") String token){
        String[] statValues = {"error","success"};
        model.addAttribute("link", centralServerUrl+"/login");
        var savedToken = emailVerificationTokenService.getToken(email);
        if (savedToken.isEmpty()) {
            model.addAttribute("status",statValues[0]);
            model.addAttribute("message", "Token verification failed, no token found for this email: "+email);
            return "verify";
        }
        if (!savedToken.get().equals(token)) {
            model.addAttribute("status",statValues[0]);
            model.addAttribute("message", "Token verification failed, wrong token provided for this email: "+email);
            return "verify";
        }

        var info = emailVerificationTokenService.getUserTokenInfo(email);
        emailVerificationTokenService.deleteToken(email);
        var user = new User(info.name(), info.email(), info.password());
        memo.save(user);
        memo.getValid(new Records.UserValidationInfo(info.id(), info.email(), info.password()));

        model.addAttribute("status",statValues[1]);
        model.addAttribute("message", "Success! Your account has been activated. Continue to login page.");

        return "verify";
    }
}