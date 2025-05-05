package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

// @EnableScheduling
// @EnableSpringJobActuator
@SpringBootApplication
public class ExampleSpringBoot30xApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringBoot30xApplication.class, args);
    }
}