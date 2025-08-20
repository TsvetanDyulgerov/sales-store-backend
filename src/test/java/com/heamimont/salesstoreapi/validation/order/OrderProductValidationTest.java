package com.heamimont.salesstoreapi.validation.order;

import com.heamimont.salesstoreapi.dto.order.OrderProductDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProductValidationTest {

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

    private OrderProductDTO buildValidOrderProduct() {
        OrderProductDTO orderProduct = new OrderProductDTO();
        orderProduct.setOrderId(1L);
        orderProduct.setProductId(2L);
        orderProduct.setProductQuantity(5);
        return orderProduct;
    }

    @Test
    void whenOrderProductIsValid_noConstraintViolations() {
        OrderProductDTO orderProduct = buildValidOrderProduct();

        Set<ConstraintViolation<OrderProductDTO>> violations = validator.validate(orderProduct);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOrderIdIsNull_constraintViolation() {
        OrderProductDTO orderProduct = buildValidOrderProduct();
        orderProduct.setOrderId(null);

        Set<ConstraintViolation<OrderProductDTO>> violations = validator.validate(orderProduct);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("orderId"))).isTrue();
    }

    @Test
    void whenProductIdIsNull_constraintViolation() {
        OrderProductDTO orderProduct = buildValidOrderProduct();
        orderProduct.setProductId(null);

        Set<ConstraintViolation<OrderProductDTO>> violations = validator.validate(orderProduct);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("productId"))).isTrue();
    }

    @Test
    void whenProductQuantityLessThanOne_constraintViolation() {
        OrderProductDTO orderProduct = buildValidOrderProduct();
        orderProduct.setProductQuantity(0);

        Set<ConstraintViolation<OrderProductDTO>> violations = validator.validate(orderProduct);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("productQuantity"))).isTrue();
    }
}
