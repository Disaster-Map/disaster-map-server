package com.disastermap.disastermapserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DisasterMapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisasterMapServerApplication.class, args);
    }

}
