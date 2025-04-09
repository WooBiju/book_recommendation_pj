package com.github.bookproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BookProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookProjectApplication.class, args);
    }

}
