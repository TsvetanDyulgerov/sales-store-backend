package com.heamimont.salesstoreapi.validation.order;

import com.heamimont.salesstoreapi.dto.order.UpdateOrderStatusDTO;
import com.heamimont.salesstoreapi.model.OrderStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateOrderStatusValidationTest {

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
    void whenStatusIsValid_noConstraintViolations() {
        UpdateOrderStatusDTO dto = new UpdateOrderStatusDTO();
        dto.setStatus(OrderStatus.PENDING);

        Set<ConstraintViolation<UpdateOrderStatusDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenStatusIsNull_constraintViolation() {
        UpdateOrderStatusDTO dto = new UpdateOrderStatusDTO();
        dto.setStatus(null);

        Set<ConstraintViolation<UpdateOrderStatusDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("status"))).isTrue();
    }
}
