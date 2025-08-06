package com.heamimont.salesstoreapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Order model class.
 */

public class OrderTests {

    private static Validator validator;
    private Order order;
    private User user;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        order = new Order();
        user = new User();
        user.setId(1L);
    }

    @Test
    void testValidOrder() {
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalCost(100.0);
        order.setStatus(OrderStatus.PENDING);

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testOrderWithNullUser() {
        order.setOrderDate(LocalDate.now());
        order.setTotalCost(100.0);
        order.setStatus(OrderStatus.PENDING);

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testSetAndGetUser() {
        order.setUser(user);
        assertEquals(user, order.getUser());
    }

    @Test
    void testSetAndGetOrderDate() {
        LocalDate orderDate = LocalDate.now();
        order.setOrderDate(orderDate);
        assertEquals(orderDate, order.getOrderDate());
    }

    @Test
    void testSetAndGetTotalCost() {
        double totalCost = 150.50;
        order.setTotalCost(totalCost);
        assertEquals(totalCost, order.getTotalCost(), 0.001);
    }

    @Test
    void testSetAndGetStatus() {
        OrderStatus status = OrderStatus.DONE;
        order.setStatus(status);
        assertEquals(status, order.getStatus());
    }

    @Test
    void testSetAndGetOrderProducts() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct product1 = new OrderProduct();
        OrderProduct product2 = new OrderProduct();
        orderProducts.add(product1);
        orderProducts.add(product2);

        order.setOrderProducts(orderProducts);
        assertEquals(orderProducts, order.getOrderProducts());
        assertEquals(2, order.getOrderProducts().size());
    }

    @Test
    void testOrderProductsInitializedEmpty() {
        assertNotNull(order.getOrderProducts());
        assertTrue(order.getOrderProducts().isEmpty());
    }

    @Test
    void testGetId() {
        assertNull(order.getId()); // ID should be null before persistence
    }
}
