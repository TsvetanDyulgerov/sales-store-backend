package com.heamimont.salesstoreapi.validation.product;

import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateProductValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidatorInstance() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void close() {
        validatorFactory.close();
    }

    @Test
    void whenProductIsValid_noConstraintViolations() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Valid Product");
        product.setDescription("Valid description");
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsBlank_constraintViolation() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName(""); // blank
        product.setDescription("Valid description");
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean nameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertThat(nameViolation).isTrue();
    }

    @Test
    void whenDescriptionTooLong_constraintViolation() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Valid Product");
        product.setDescription("A".repeat(500)); // too long
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean descriptionViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertThat(descriptionViolation).isTrue();
    }

    @Test
    void whenActualPriceIsNull_constraintViolation() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Valid Product");
        product.setDescription("Valid description");
        product.setActualPrice(null); // required
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean actualPriceViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("actualPrice"));
        assertThat(actualPriceViolation).isTrue();
    }

    @Test
    void whenSellingPriceNegative_constraintViolation() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Valid Product");
        product.setDescription("Valid description");
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(-5)); // invalid
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean sellingPriceViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("sellingPrice"));
        assertThat(sellingPriceViolation).isTrue();
    }

    @Test
    void whenAvailableQuantityNegative_constraintViolation() {
        CreateProductDTO product = new CreateProductDTO();
        product.setName("Valid Product");
        product.setDescription("Valid description");
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(-1); // invalid

        Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean quantityViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("availableQuantity"));
        assertThat(quantityViolation).isTrue();
    }
}
