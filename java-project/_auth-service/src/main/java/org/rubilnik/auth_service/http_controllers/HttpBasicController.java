package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.NetworkInterface;


@EnableAspectJAutoProxy
@Controller
//@CrossOrigin("*")
public class HttpBasicController {
    @Autowired
    WebServerApplicationContext context;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;
    @Value("${rubilnik.lan.roomPort:#{null}}") // optional
    String roomPort;
    @Value("${rubilnik.lan.qr-string:#{null}}") // optional
    String lanQrString;

    @GetMapping(value = { "/", "/login", "/register", "/edit-quiz/*", "/play", "/play/*", "/join"})
    String reactUI(Model model){
        var localIp = getLocalIP();
        var authPort = context.getWebServer().getPort();
        var springEnv = context.getEnvironment();
        model.addAttribute("localIP",localIp);
        model.addAttribute("authPort",authPort);
        model.addAttribute("roomPort",roomPort);
        model.addAttribute("serverProfiles",springEnv.getActiveProfiles());
        model.addAttribute("lanQrString",lanQrString);
        return "index";
    }

    String getLocalIP() {
        try {
            var interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                var iface = interfaces.nextElement();
                if (iface.isUp() && !iface.isLoopback()) {
                    var interfaceAddresses = iface.getInterfaceAddresses();
                    for (var address : interfaceAddresses) {
                        if (address.getAddress().isSiteLocalAddress() && address.getAddress().getHostAddress().contains("192.168.0"))
                            return address.getAddress().getHostAddress();  // getCanonicalHostName() // "user_pc"
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }
}