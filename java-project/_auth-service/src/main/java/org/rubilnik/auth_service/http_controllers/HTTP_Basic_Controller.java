package org.rubilnik.auth_service.http_controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.rubilnik.auth_service.App;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.UserRegisterEmailVerificationTokenService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.NetworkInterface;


@EnableAspectJAutoProxy
@Controller
//@CrossOrigin("*")
public class HTTP_Basic_Controller {
    @Autowired
    WebServerApplicationContext context;
    @Autowired
    UserRegisterEmailVerificationTokenService userRegisterEmailVerificationTokenService;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;

//  React UI provider
    @GetMapping(value = { "/", "/login", "/register", "/edit-quiz/*", "/play", "/play/*", "/join"})
    String ui(Model model){
        var localIp = getLocalIP();
        var authPort = context.getWebServer().getPort();
        var springEnv = context.getEnvironment();
        var roomPort = springEnv.getProperty("rubilnik.lan.roomPort");

        model.addAttribute("localIP",localIp);
        model.addAttribute("authPort",authPort);
        model.addAttribute("roomPort",roomPort);
        model.addAttribute("serverProfiles",springEnv.getActiveProfiles());

        model.addAttribute("lanQrString",springEnv.getProperty("rubilnik.lan.qr-string"));
        return "index";
    }

    @ResponseBody()
    @PostMapping("/hi")
    String postGreeting(){
        return App.getGreeting();
    }

    String getLocalIP() {
        try {
            var interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                var iface = interfaces.nextElement();
                if (iface.isUp() && !iface.isLoopback()) {
                    var interfaceAddresses = iface.getInterfaceAddresses();
                    for (var address : interfaceAddresses) {
                        if (address.getAddress().isSiteLocalAddress() && address.getAddress().getHostAddress().contains("192.168.0")) {
                            return address.getAddress().getHostAddress();  // getCanonicalHostName() // "user_pc"
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }
}