package org.rubilnik.auth_service.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HTTP_Controller_LoggerAspect {
    
    @Autowired
    private HttpServletRequest request;

    @Before("execution(* org.rubilnik.auth_service.http_controllers.*.*(..))")
    void log(){
        System.out.println(request.getMethod()+" request received for "+request.getRequestURI());
    }
}