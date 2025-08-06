package com.heamimont.salesstoreapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.heamimont.salesstoreapi.repository")
@EntityScan(basePackages = "com.heamimont.salesstoreapi.model")
public class TestConfig {
}