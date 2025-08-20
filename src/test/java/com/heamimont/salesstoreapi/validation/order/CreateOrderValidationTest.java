package com.heamimont.salesstoreapi.validation.order;

import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderProductDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateOrderValidationTest {

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

    private CreateOrderDTO buildValidOrder() {
        OrderProductDTO orderProduct = new OrderProductDTO();
        orderProduct.setOrderId(1L);
        orderProduct.setProductId(2L);
        orderProduct.setProductQuantity(3);

        CreateOrderDTO order = new CreateOrderDTO();
        order.setOrderProducts(List.of(orderProduct));
        return order;
    }

    @Test
    void whenOrderIsValid_noConstraintViolations() {
        CreateOrderDTO order = buildValidOrder();

        Set<ConstraintViolation<CreateOrderDTO>> violations = validator.validate(order);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOrderProductsEmpty_constraintViolation() {
        CreateOrderDTO order = buildValidOrder();
        order.setOrderProducts(List.of()); // empty list

        Set<ConstraintViolation<CreateOrderDTO>> violations = validator.validate(order);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("orderProducts"))).isTrue();
    }

    @Test
    void whenOrderProductsContainInvalidItem_constraintViolation() {
        OrderProductDTO invalidProduct = new OrderProductDTO();
        invalidProduct.setOrderId(null); // invalid

        CreateOrderDTO order = buildValidOrder();
        order.setOrderProducts(List.of(invalidProduct));

        Set<ConstraintViolation<CreateOrderDTO>> violations = validator.validate(order);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().startsWith("orderProducts"))).isTrue();
    }
}
