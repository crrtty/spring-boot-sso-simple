package com.ly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * web-a
 */
@SpringBootApplication(scanBasePackages = {"com.ly"})
public class AApplication {

    public static void main( String[] args ) {
        SpringApplication.run(AApplication.class, args);
    }
}
