package com.springboot.Spring_Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


//This is if we want to have our own Spring Security Configuration apart from
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Lambda Way
//    @Bean // Spring will create object for you by Default
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception{



        security.csrf(customizer -> customizer.disable()); // This will remove CSRF Token
        security.authorizeHttpRequests(request->request.anyRequest().authenticated()); // This will create Basic Auth but it will not create input form
        security.formLogin(Customizer.withDefaults()); // This will give the basic Login form and Logout Route
        security.httpBasic(Customizer.withDefaults()); // This will make to send Auth everytime we request. (Best for Postman)

        //This will nulify the use of Form Login and we need BasicAuth everytime we request
        security.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // This will create new session everyutime and make it stateless

        return security.build();  // This will bypass and create a new Session by Default
    }
    //Imperitive Way
    @Bean // Spring will create object for you by Default
    public SecurityFilterChain securityFilterChain1(HttpSecurity security) throws Exception{

        //Disable CSRF
        Customizer<CsrfConfigurer<HttpSecurity>> custCsrf=new Customizer<CsrfConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CsrfConfigurer<HttpSecurity> configure) {
                configure.disable();
            }
        };
        security.csrf(custCsrf);


        // Basic Auth
        Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> custHttp=new Customizer<AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                registry.anyRequest().authenticated();
            }
        };
        security.authorizeHttpRequests(custHttp);


        return security.build();  // This will bypass and create a new Session by Default
    }
}
