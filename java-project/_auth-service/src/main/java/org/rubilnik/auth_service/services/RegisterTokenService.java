package org.rubilnik.auth_service.services;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller;
import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service @Profile("server")
public class RegisterTokenService {
    @Autowired
    PasswordEncoder emailEncoder;
    // TODO token TimeToLive?
    // key=email, value
    private final Map<String,Records.UserTokenInfo> tokens = new HashMap<>();

    public String generateToken(String username, Records.UserValidationInfo info){
        var token = emailEncoder.encode(info.email());
        var userTokenInfo = new Records.UserTokenInfo(info.id(),info.email(),info.password(), username, token);
        tokens.put(info.email(), userTokenInfo);
        return token;
    }
    public String getToken(String email){
        return tokens.get(email).token();
    }
    public Records.UserTokenInfo getUserTokenInfo(String email){
        return tokens.get(email);
    }
    public void deleteToken(String email){
        tokens.remove(email);
    }
}
