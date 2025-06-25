package org.rubilnik.auth_service.configs;

import org.rubilnik.auth_service.services.UserValidationDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.net.http.HttpRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain securityAccess(HttpSecurity filterConfig) throws Exception{
        return filterConfig
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity (enable in production)
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(customizer->customizer
                        .requestMatchers(HttpMethod.POST,"/user")
                            .permitAll()
                        .requestMatchers("/public/**","/assets/**", "/templates/**", "/favicon.ico", "/login", "/register", "/verify", "/user/login", "/hi", "/join/**", "/play/**")
                            .permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, authException) ->
                                res.sendRedirect("/login"))
                )
                //disable to use custom POST /login endpoint with Authentication and SecurityContextHolder

                .logout(customizer->customizer
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, res, auth) -> res.sendRedirect("/login"))
                )
                .build();
    }
    @Bean
    UserValidationDetailsService userValidationDetailsService(){
        return new UserValidationDetailsService();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
