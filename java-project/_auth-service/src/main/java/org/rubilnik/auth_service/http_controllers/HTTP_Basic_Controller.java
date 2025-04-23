package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.App;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@EnableAspectJAutoProxy
@RestController
public class HTTP_Basic_Controller {

    @GetMapping("/hi")
    String greeting() {
        System.out.println("GET /hi request");
        return App.getGreeting();
    }

}