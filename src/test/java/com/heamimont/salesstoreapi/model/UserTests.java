package com.heamimont.salesstoreapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password1");
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid");
    }

    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@@example"); // invalid email
        user.setPassword("password1");
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should have email validation errors");
    }

    @Test
    void testInvalidPassword() {
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("pass"); // invalid password
        user.setRole(Role.ADMIN);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should have password validation errors");
    }
}
