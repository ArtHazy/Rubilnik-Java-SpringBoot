package org.rubilnik.auth_service.services;

import org.rubilnik.core.users.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service @Profile("server")
public class UserHttpSessionTokenManager {
    // key = token, value = userId
    private Map<String,String> userTokenMap = new HashMap<>();

    public String createToken(String userId){
        var token = UUID.randomUUID().toString();
        userTokenMap.put(token,userId);
        return token;
    }
    public Optional<String> getUserId(String token){
        return Optional.ofNullable(userTokenMap.get(token));
    }
    public void deleteToken(String token){
        userTokenMap.remove(token);
    }
}
