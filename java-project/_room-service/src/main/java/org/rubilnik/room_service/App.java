package org.rubilnik.room_service;

import org.rubilnik.core.users.User;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;


@SpringBootApplication
public class App {
    static class UserValidationInfo{
        public String id,password,email;
    }
    static class PostUserVerificationJsonBody{
        public UserValidationInfo validation;
    }
    // private static boolean useAuthentication = !System.getenv("AUTH_SERVICE_URL").isBlank();
    private static RestClient auth_service_rest_client = RestClient.builder().baseUrl( System.getenv("AUTH_SERVICE_URL") ).build();
 
    static boolean validateUser(UserValidationInfo info) {
        try {
            var body = new PostUserVerificationJsonBody();
            body.validation = info;
            var res = auth_service_rest_client.post().uri("/user/verify").body(body).retrieve().toEntity(Object.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
            // TODO: handle exception
        }
    }
    static User getUser(UserValidationInfo info) throws RestClientResponseException{
        var body = new PostUserVerificationJsonBody();
        body.validation = info;
        var res = auth_service_rest_client.post().uri("/user/get").body(body).retrieve().toEntity(User.class);
        return res.getBody();
    }

    public static String getGreeting(){
        return "Hello room service!";
    }
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}