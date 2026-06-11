package com.springboot.Spring_Security.Controller;

import com.springboot.Spring_Security.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {
    List<Student> students=new ArrayList<>(List.of(
            new Student(1,"Aditya","Java Springboot"),
            new Student(2,"Anushka", "5G Networks")
    ));

    // GET request does not need any CSRF token
    @GetMapping("/students")
    public List<Student> getAllStudents(){
        return students;
    }
    // This will not work in Postman if we do not use CSRF Token
    // This will work in browser as browser itself will provide the CSRF token
    // Though this is a post mapping and we are not submitting any Student Object so this will not work DELETE mapping will work
    @PostMapping("/student")
    public void addStudent(@RequestBody Student student){
        students.add(student);
    }

    // Method to get CSRF token
    @GetMapping("csrf-token")
    public CsrfToken getCSRFToken(HttpServletRequest httpRequest){
        return (CsrfToken) httpRequest.getAttribute("_csrf");
    }

    // This will work in browser as browser itself will provide the CSRF token
    @DeleteMapping("/student")
    public void removeStudent(){
        students.removeLast();
    }
}
