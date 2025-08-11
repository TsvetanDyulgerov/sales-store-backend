package com.heamimont.salesstoreapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void whenOrderIsValid_noConstraintViolations() {
        User user = new User();
        user.setId(1L);
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test testProduct description");
        product.setActualPrice(BigDecimal.valueOf(99.99));
        product.setSellingPrice(BigDecimal.valueOf(149.99));
        product.setAvailableQuantity(10);

        Order testOrder = new Order();
        testOrder.setUser(user);
        testOrder.setOrderDate(LocalDate.now());
        testOrder.setTotalCost(BigDecimal.valueOf(99.99));
        testOrder.setStatus(OrderStatus.PENDING);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(testOrder);
        orderProduct.setProduct(product);

        List<OrderProduct> orderProducts = new ArrayList<OrderProduct>();
        orderProducts.add(orderProduct);

        testOrder.setOrderProducts(orderProducts);

        Set<ConstraintViolation<Order>> violations = validator.validate(testOrder);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenUserIsNull_constraintViolation() {
        Order order = new Order();
        order.setUser(null);  // user is null, should trigger violation
        order.setOrderDate(LocalDate.now());
        order.setTotalCost(BigDecimal.valueOf(99.99));
        order.setStatus(OrderStatus.PENDING);

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertThat(violations).isNotEmpty();

        boolean userViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("user"));
        assertThat(userViolation).isTrue();
    }

    @Test
    void whenOrderDateIsNull_constraintViolation() {
        User user = new User();
        user.setId(1L);
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(null);  // null order date
        order.setTotalCost(BigDecimal.valueOf(99.99));
        order.setStatus(OrderStatus.PENDING);

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertThat(violations).isNotEmpty();

        boolean dateViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("orderDate"));
        assertThat(dateViolation).isTrue();
    }

    @Test
    void whenStatusIsNull_constraintViolation() {
        User user = new User();
        user.setId(1L);
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalCost(BigDecimal.valueOf(99.99));
        order.setStatus(null);  // null status

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertThat(violations).isNotEmpty();

        boolean statusViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("status"));
        assertThat(statusViolation).isTrue();
    }

     @Test
     void whenTotalCostNegative_constraintViolation() {
         User user = new User();
         user.setId(1L);
         user.setUsername("validUser");
         user.setFirstName("John");
         user.setLastName("Doe");
         user.setEmail("john.doe@example.com");
         user.setPassword("abc12345");
         user.setRole(Role.USER);

         Order order = new Order();
         order.setUser(user);
         order.setOrderDate(LocalDate.now());
         order.setTotalCost(BigDecimal.valueOf(-10)); // invalid negative cost
         order.setStatus(OrderStatus.PENDING);

         Set<ConstraintViolation<Order>> violations = validator.validate(order);
         assertThat(violations).isNotEmpty();

         boolean totalCostViolation = violations.stream()
             .anyMatch(v -> v.getPropertyPath().toString().equals("totalCost"));
         assertThat(totalCostViolation).isTrue();
     }
}
