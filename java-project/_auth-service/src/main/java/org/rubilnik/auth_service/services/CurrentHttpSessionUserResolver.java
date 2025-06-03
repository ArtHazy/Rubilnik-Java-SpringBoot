package org.rubilnik.auth_service.services;

import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentHttpSessionUserResolver {
    @Autowired
    UserMemoService memo;

    public User getCurrent(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth==null||!auth.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        return memo.get(null, auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't find user with such email"));
    }
}
