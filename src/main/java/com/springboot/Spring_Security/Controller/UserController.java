package com.springboot.Spring_Security.Controller;

import com.springboot.Spring_Security.model.User;
import com.springboot.Spring_Security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    //This is a part of Spring Security
    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12); // 12 is the Rounds
    // Or you could remove new and write it as a bean and define its values
    @PostMapping("/register")
    public User register(@RequestBody User user){
        // Bcrypt it here when Registering the User
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.SaveUser(user);
    }
}
