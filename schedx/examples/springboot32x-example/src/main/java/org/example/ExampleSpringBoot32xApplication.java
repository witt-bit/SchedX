package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableScheduling
// @EnableSpringJobActuator
@SpringBootApplication
public class ExampleSpringBoot32xApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringBoot32xApplication.class, args);
    }


    // @EventListener(ContextClosedEvent.class)
    // public void on(ContextClosedEvent e){
    //     Supplier<ObservationRegistry> observationRegistrySupplier;
    //     ScheduledAnnotationBeanPostProcessor;
    //     ScheduledTaskRegistrar;
    //     System.out.println("00000");
    // }
}