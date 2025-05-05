package org.example;

import org.apache.commons.logging.impl.NoOpLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

// @EnableScheduling
// @EnableSpringJobActuator
@SpringBootApplication
public class ExampleSpringBoot23xApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringBoot23xApplication.class, args);
    }




    // @EventListener
    // public void aa(){
    //     // ScheduledAnnotationBeanPostProcessor
    //     // Supplier<ObservationRegistry> observationRegistrySupplier;
    // }
}