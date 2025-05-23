package org.rubilnik.auth_service.http_controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.rubilnik.auth_service.App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.NetworkInterface;
import java.nio.file.Paths;


@EnableAspectJAutoProxy
@Controller
@CrossOrigin("*")
public class HTTP_Basic_Controller {
    @Autowired
    WebServerApplicationContext context;

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
    @GetMapping("/hi")
    String greeting() {
        System.out.println("GET /hi request");
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