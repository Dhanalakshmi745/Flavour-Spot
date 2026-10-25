package com.flavorspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for Flavor Spot Restaurant Management System.
 *
 * @SpringBootApplication encapsulates:
 * 1. @Configuration - tags the class as a source of bean definitions.
 * 2. @EnableAutoConfiguration - tells Spring Boot to automatically configure beans based on classpath settings.
 * 3. @ComponentScan - scans the current package (com.flavorspot) and subpackages for @Component, @Service, @Repository, and @RestController.
 */
@SpringBootApplication
public class FlavorSpotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlavorSpotApplication.class, args);
    }
}
