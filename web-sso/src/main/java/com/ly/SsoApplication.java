package com.ly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SSO
 */
@SpringBootApplication(scanBasePackages = {"com.ly"})
public class SsoApplication {
    public static void main( String[] args ) {
        SpringApplication.run(SsoApplication.class, args);
    }
}
