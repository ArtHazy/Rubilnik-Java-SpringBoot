package org.rubilnik.auth_service;

import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;

@Configuration
public class ServiceConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain securityAccess(HttpSecurity http) throws Exception{
        return http
            // CSRF (Cross-Site Request Forgery) is an attack where a malicious website tricks a user's browser
            // into making a request to another site where the user is already authenticated (e.g., your app),
            // without their knowledge using user's cookie.
            .csrf(csrf -> csrf.disable())
            // Post request processing
            .formLogin(form -> form.disable()
//                .loginProcessingUrl("/login")
            )
            // Check for "Authorization: Basic <token>" header
            .httpBasic(httpBasic -> {} )
            // Allowed paths without authorization
            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login", "/register", "/hi").permitAll()
                .anyRequest().permitAll()
            )
            .build();
    }
//    @Bean
//    UserValidationDetailsService userValidationDetailsService(){
//        return new UserValidationDetailsService();
//    }
}
