package com.example.springbootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone; // <-- Make sure to import this

@SpringBootApplication
public class SpringBootProjectApplication {

    public static void main(String[] args) {
        // Force the JVM to use the modern timezone name that PostgreSQL recognizes
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        SpringApplication.run(SpringBootProjectApplication.class, args);
    }

}