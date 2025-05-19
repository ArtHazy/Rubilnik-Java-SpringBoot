package org.rubilnik.auth_service;

import org.rubilnik.auth_service.repositories.UserRepository;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
public class        UserValidationDetailsService implements UserDetailsService {
//    @Autowired
    UserMemoService memo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = memo.getByName(username);
        if (user==null) throw new UsernameNotFoundException("User with such name wasn't found");
        return User.withUsername(user.getName())
            .password(user.getPassword())
            .roles("default")
            .build();
    }
}
