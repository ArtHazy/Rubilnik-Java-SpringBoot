package org.rubilnik.auth_service.http_controllers;

import jdk.jfr.ContentType;
import org.rubilnik.auth_service.App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.NetworkInterface;
import java.nio.file.Paths;

@EnableAspectJAutoProxy
@Controller
public class HTTP_Basic_Controller {
    @Autowired
    WebServerApplicationContext context;

    @GetMapping(value = { "/", "/login", "/register", "/edit-quiz/*", "/play", "/play/*", "/join"})
    String ui(Model model){
        var localIp = getLocalIP();
        var serverPort = context.getWebServer().getPort();
//        var networkQR = "http://"+localIp+":"+serverPort; //context.getEnvironment().getProperty("networkQR");
        System.out.println(localIp+":"+serverPort);

        model.addAttribute("localIP",localIp);
        model.addAttribute("serverPort",serverPort);
//        model.addAttribute("networkQR",networkQR);

        return "index";
    }

    @ResponseBody()
    @GetMapping("/hi")
    String greeting() {
        System.out.println("GET /hi request");
        return App.getGreeting();
    }

    String getLocalIP(){
        try{
            var interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()){
                var iface = interfaces.nextElement();
                if ( iface.isUp() && !iface.isLoopback() ){
                    var interfaceAddresses = iface.getInterfaceAddresses();
                    for (var address:interfaceAddresses){
                        if (address.getAddress().isSiteLocalAddress()){
                            return address.getAddress().getCanonicalHostName();
                        }
                    }
                }
            }
        } catch (Exception e){}
        return "failed";
    }
}