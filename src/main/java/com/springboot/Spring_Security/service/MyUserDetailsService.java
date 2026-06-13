package com.springboot.Spring_Security.service;

import com.springboot.Spring_Security.model.User;
import com.springboot.Spring_Security.model.UserPrincipal;
import com.springboot.Spring_Security.repository.UserRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// This is a class that implements UserDetailsService functional interface to create a function to load User into the Spring Security
@Service
public class MyUserDetailsService  implements UserDetailsService {
    @Autowired
    private UserRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=repo.findByUsername(username);
        if(user==null){
            System.out.println("user not Found");
            throw new UsernameNotFoundException("User 404");
        }
        return new UserPrincipal(user);
    }
}
