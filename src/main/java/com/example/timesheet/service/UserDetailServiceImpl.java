package com.example.timesheet.service;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       com.example.timesheet.entities.User user = userService.loadUserByEmail(email);
        if(user==null) throw new UsernameNotFoundException(String.format("User %s not found",email));
        String[] roles=user.getRoles().stream().map(u->u.getRole()).toArray(String[]::new);
        UserDetails userDetails= User

                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roles).build();
        return userDetails;
    }


}
