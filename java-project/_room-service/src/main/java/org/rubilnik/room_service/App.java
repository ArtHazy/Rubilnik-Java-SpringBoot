package org.rubilnik.room_service;

import org.rubilnik.core.users.User;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
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

    private static RestClient auth_service_rest_client;
 
    static boolean validateUser(String token) {
        try {
            var res = auth_service_rest_client.post().uri("/user/verify").body(token).retrieve().toEntity(Object.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
            // TODO: handle exception
        }
    }
    static User resolveUser(String sessionId) throws RestClientResponseException{
            return auth_service_rest_client.get()
                    .uri("/user/validate")
                    .header(HttpHeaders.COOKIE, "JSESSIONID=" + sessionId)
                    .retrieve().toEntity(User.class).getBody();
    }

    public static String getGreeting(){
        return "Hello room service!";
    }
    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);
        var env = context.getEnvironment();
        auth_service_rest_client = RestClient.builder().baseUrl( env.getProperty("rubilnik.auth-service-url") ).build();
    }
}