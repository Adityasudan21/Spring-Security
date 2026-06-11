package com.springboot.Spring_Security.Controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    //We are using Spring security so we will get a Simple Basic Auth Form on hitting any URL for first time.
    // User name is "user" BY DEFAULT
    //Password is in the Console of the server

    // You can set your own password by adding that in the Application.properties
    @GetMapping("/hello")
    public String greeting(){
        return "Hellow";
    }
    //HttpServletRequest Lets you get information of the Request like Session ID
    @GetMapping("/SessionInfo")
    public String Sessionfunc(HttpServletRequest httprequest){
        return "Session ID is: "+ httprequest.getSession().getId(); //This will give the Session ID
    }
    // Also another Mapping is there from Spring Security that is /logout
    // Which displays if you want to logout and ends your session
}
