package com.heamimont.salesstoreapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalesStoreApiApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SalesStoreApiApplication.class);
        // UPDATE_THIS_BEFORE_USE
        // prod - for production use
        // dev - for development use
        app.setAdditionalProfiles("prod");
        app.run(args);
    }

}
