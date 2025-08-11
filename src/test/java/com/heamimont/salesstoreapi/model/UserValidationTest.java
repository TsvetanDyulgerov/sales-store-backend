package com.heamimont.salesstoreapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTest {

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
    void whenUserIsValid_noConstraintViolations() {
        User user = new User();
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailIsInvalid_constraintViolation() {
        User user = new User();
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("invalid-email"); // invalid format
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();

        boolean emailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertThat(emailViolation).isTrue();
    }

    @Test
    void whenPasswordIsInvalid_constraintViolation() {
        User user = new User();
        user.setUsername("validUser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("short"); // too short, no digit
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();

        boolean passwordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertThat(passwordViolation).isTrue();
    }

    @Test
    void whenUsernameIsNull_constraintViolation() {
        User user = new User();
        user.setUsername(null); // null username
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("abc12345");
        user.setRole(Role.USER);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();

        boolean usernameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        assertThat(usernameViolation).isTrue();
    }
}
