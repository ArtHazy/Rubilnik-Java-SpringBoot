package org.rubilnik.auth_service.services;

import org.rubilnik.auth_service.services.userMemo.UserMemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserValidationDetailsService implements UserDetailsService {
    @Autowired
    UserMemo memo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserValidationDetailsService");
        var user = memo.get(null,username).orElseThrow(()->new UsernameNotFoundException("User with such email wasn't found"));
        System.out.println("!"+user.getEmail()+" "+user.getPassword());
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("def")
                .build();
    }
}
