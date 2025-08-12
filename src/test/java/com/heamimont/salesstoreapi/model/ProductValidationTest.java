package com.heamimont.salesstoreapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validProduct_shouldHaveNoViolations() {
        Product product = new Product(
                1L,
                "Laptop",
                "High performance laptop",
                new BigDecimal("1000.00"),
                new BigDecimal("1200.00"),
                10
        );

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertTrue(violations.isEmpty(), "Product should be valid with correct values");
    }

    @Test
    void blankName_shouldFailValidation() {
        Product product = new Product(
                1L,
                " ",
                "Some description",
                new BigDecimal("100.00"),
                new BigDecimal("150.00"),
                5
        );

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void negativePrice_shouldFailValidation() {
        Product product = new Product(
                1L,
                "Phone",
                "Smartphone",
                new BigDecimal("-10.00"),
                new BigDecimal("150.00"),
                5
        );

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("actualPrice")));
    }

    @Test
    void negativeQuantity_shouldFailValidation() {
        Product product = new Product(
                1L,
                "Tablet",
                "Tablet device",
                new BigDecimal("200.00"),
                new BigDecimal("250.00"),
                -5
        );

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("availableQuantity")));
    }
}
