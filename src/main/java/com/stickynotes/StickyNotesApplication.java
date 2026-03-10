package com.stickynotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ENTRY POINT — This is where Spring Boot starts.
 *
 * @SpringBootApplication is a shortcut for three things:
 *   1. @Configuration      — marks this as a config class
 *   2. @EnableAutoConfiguration — tells Spring Boot to auto-configure itself
 *      (this is why we don't need to set up Tomcat, Jackson etc. manually)
 *   3. @ComponentScan      — tells Spring to find all our @Controller,
 *      @Service classes automatically
 *
 * Syllabus link: "Introduction to Spring Boot", "Auto-configuration"
 */
@SpringBootApplication
public class StickyNotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(StickyNotesApplication.class, args);
        System.out.println("✅ Sticky Notes app is running at http://localhost:8080");
    }
}
