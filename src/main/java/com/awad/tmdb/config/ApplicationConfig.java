package com.awad.tmdb.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
     @Bean
     CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("This method will run right on application startup!");
        };
    }

    // Define other beans
}
