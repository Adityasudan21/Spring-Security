package com.springboot.Spring_Security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class DaoSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authProvide(){
        // For Database we have a DAO authentication Provide
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
//        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return provider;
    }


    @Bean // Spring will create object for you by Default
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception{
        security.csrf(customizer -> customizer.disable()) // This will remove CSRF Token
        .authorizeHttpRequests(request->request.anyRequest().authenticated()) // This will create Basic Auth but it will not create input form
        .httpBasic(Customizer.withDefaults()) // This will make to send Auth everytime we request. (Best for Postman)
        .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // This will create new session everyutime and make it stateless
        return security.build();  // This will bypass and create a new Session by Default
    }
}
