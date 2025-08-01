package com.security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VulnerableSpringApp {
    public static void main(String[] args) {
        // VULNERABLE: Log4Shell potential trigger
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        SpringApplication.run(VulnerableSpringApp.class, args);
    }
}
