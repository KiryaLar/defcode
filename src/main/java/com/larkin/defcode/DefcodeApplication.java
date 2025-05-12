package com.larkin.defcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DefcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefcodeApplication.class, args);
    }
}
