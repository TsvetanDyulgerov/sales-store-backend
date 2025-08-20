package com.heamimont.salesstoreapi.validation.product;

import com.heamimont.salesstoreapi.dto.product.UpdateProductDTO;
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

public class UpdateProductValidationTest {

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
        UpdateProductDTO product = new UpdateProductDTO();
        product.setName("Valid Product");
        product.setDescription("Valid description");
        product.setActualPrice(BigDecimal.valueOf(10.5));
        product.setSellingPrice(BigDecimal.valueOf(12.0));
        product.setAvailableQuantity(5);

        Set<ConstraintViolation<UpdateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenActualPriceNegative_constraintViolation() {
        UpdateProductDTO product = new UpdateProductDTO();
        product.setName("Valid Product");
        product.setActualPrice(BigDecimal.valueOf(-10.5)); // invalid

        Set<ConstraintViolation<UpdateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isNotEmpty();
        boolean actualPriceViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("actualPrice"));
        assertThat(actualPriceViolation).isTrue();
    }

    @Test
    void whenFieldsNull_noConstraintViolations() {
        UpdateProductDTO product = new UpdateProductDTO();
        // all optional, should pass
        Set<ConstraintViolation<UpdateProductDTO>> violations = validator.validate(product);
        assertThat(violations).isEmpty();
    }
}
