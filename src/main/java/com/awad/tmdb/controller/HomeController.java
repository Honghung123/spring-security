package com.awad.tmdb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping("")
    public String home() {
        // Return a simple html welcome page
        return """
                <h1> TMDB API </h1>
                <p> Welcome to the TMDB API </p> 
                <p> Click here to login: <a href="/auth/login"><b>Login</b></a> </p>
                """ ;
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        // Return a simple html welcome page
        return """
                Login successfully
                """ ;
    }

}
