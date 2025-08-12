package com.heamimont.salesstoreapi.model;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderProductTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenOrderProductIsValid_noConstraintsValidation() {
        OrderProduct op = new OrderProduct();
        op.setId(new OrderProductKey(1L, 2L));
        op.setOrder(new Order());
        op.setProduct(new Product());
        op.setProductQuantity(1);

        Set<ConstraintViolation<OrderProduct>> violations = validator.validate(op);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_productQuantityPositive() {
        OrderProduct op = new OrderProduct();
        op.setOrder(new Order());
        op.setProduct(new Product());

        op.setProductQuantity(-1);  // Invalid

        Set<ConstraintViolation<OrderProduct>> violations = validator.validate(op);
        assertFalse(violations.isEmpty());
        boolean hasPositiveViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("productQuantity"));
        assertTrue(hasPositiveViolation);
    }

    @Test
    void testValidation_orderAndProductNotNull() {
        OrderProduct op = new OrderProduct();
        op.setProductQuantity(1);

        // order and product are null -> violations expected
        Set<ConstraintViolation<OrderProduct>> violations = validator.validate(op);
        assertFalse(violations.isEmpty());

        boolean orderViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("order"));
        boolean productViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("product"));

        assertTrue(orderViolation);
        assertTrue(productViolation);
    }

    @Test
    void testEqualsAndHashCode() {
        OrderProductKey key1 = new OrderProductKey(1L, 2L);
        OrderProductKey key2 = new OrderProductKey(1L, 2L);
        OrderProductKey key3 = new OrderProductKey(2L, 3L);

        OrderProduct op1 = new OrderProduct();
        op1.setId(key1);

        OrderProduct op2 = new OrderProduct();
        op2.setId(key2);

        OrderProduct op3 = new OrderProduct();
        op3.setId(key3);

        assertEquals(op1, op2);
        assertEquals(op1.hashCode(), op2.hashCode());

        assertNotEquals(op1, op3);
        assertNotEquals(op1.hashCode(), op3.hashCode());
    }
}
