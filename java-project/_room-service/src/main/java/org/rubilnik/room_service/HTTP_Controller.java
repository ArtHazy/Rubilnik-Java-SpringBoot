package org.rubilnik.room_service;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class HTTP_Controller {
    @GetMapping("/hi")
    String greeting() {
        System.out.println("GET /hi request");
        return App.getGreeting();
    }
}