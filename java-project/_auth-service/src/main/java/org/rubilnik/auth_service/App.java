package org.rubilnik.auth_service;

import org.rubilnik.auth_service.repositories.UserRepository;
import org.rubilnik.core.users.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// main application class
@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.rubilnik.auth_service.repositories")
@EntityScan(basePackages = "org.rubilnik.core")
public class App {
    public static String getGreeting(){
        return "Hello auth service!";
    }

    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);
        var userRepository = context.getBean(UserRepository.class);
        // get and check existing id's from database
        var users = userRepository.findAll();
        users.forEach((user)->{
            User.getIdManager().putId(user.getId());
        });
        // create and persist def users if dont exist
        if (!userRepository.findByEmail("admin").isPresent()) {
            var user = new User(System.getenv("RUBILNIK_DEF_USR_NAME"),System.getenv("RUBILNIK_DEF_USR_EMAIL"), System.getenv("RUBILNIK_DEF_USR_PASSWORD"));
            userRepository.save(user);
        }        
    }
}