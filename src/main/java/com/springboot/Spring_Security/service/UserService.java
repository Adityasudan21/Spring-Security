package com.springboot.Spring_Security.service;

import com.springboot.Spring_Security.model.User;
import com.springboot.Spring_Security.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    public User SaveUser(User user){
        return repo.save(user);
    }
}
