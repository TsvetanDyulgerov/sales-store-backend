package com.heamimont.salesstoreapi.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.heamimont.salesstoreapi.model")
@EnableJpaRepositories("com.heamimont.salesstoreapi.repository")
public class SalesStoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SalesStoreApiApplication.class, args);
    }
}
