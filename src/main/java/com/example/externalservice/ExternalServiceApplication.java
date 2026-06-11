package com.example.externalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExternalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalServiceApplication.class, args);
    }

}
