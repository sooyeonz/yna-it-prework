package com.yna.itprework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ItPreworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItPreworkApplication.class, args);
    }

}
