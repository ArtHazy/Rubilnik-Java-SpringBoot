package org.rubilnik.auth_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// main application class
@SpringBootApplication
public class App {
    public static String getGreeting(){
        return "Hello auth service!";
    }
    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);
        var env = context.getEnvironment();
        // get and check existing id's from memory
        var userMemo = context.getBean(UserMemoService.class);
        if (env.matchesProfiles("server")){
            var users = userMemo.getAll();
            users.forEach((u)->{
                User.getIdManager().putId(u.getId());
            });
        }
        // create and save default user if doesnt exist
        if (!userMemo.get("AAAA", null).isPresent()) {
            // System.getenv заменен ~на @Value()~ на ConfigurableEnvironment env для кастомизации запуска с помощью параметров в java -jar (String[] args)
            // Spring добавляет эти параметры в контекст в приоритете над application.properties
            var defUser = new User(env.getProperty("rubilnik.def-usr.name"),env.getProperty("rubilnik.def-usr.email"),env.getProperty("rubilnik.def-usr.password"));
            userMemo.save(defUser);
        }
    }
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void logObjectAsJson(Object body){
        try {
            System.out.println(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {System.out.println("failed to write json");}
    }
}