package org.example;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.function.Supplier;

// @EnableScheduling
// @EnableSpringJobActuator
@SpringBootApplication
public class ExampleSpringBoot34xApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringBoot34xApplication.class, args);
    }


    // @EventListener(ContextClosedEvent.class)
    // public void on(ContextClosedEvent e){
    //     Supplier<ObservationRegistry> observationRegistrySupplier;
    //     ;
    //     ObservationRegistry
    //     System.out.println("00000");
    // }
}