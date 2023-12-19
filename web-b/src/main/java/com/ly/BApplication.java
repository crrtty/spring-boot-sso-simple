package com.ly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * web-b
 */
@SpringBootApplication(scanBasePackages = {"com.ly"})
public class BApplication {
    public static void main( String[] args ) {
        SpringApplication.run(BApplication.class, args);
    }
}
