package com.heamimont.salesstoreapi.validation.user;

import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserValidationTest {

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
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");

        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailIsInvalid_constraintViolation() {
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("invalid-email"); // invalid format
        userDTO.setPassword("abc12345");

        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean emailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertThat(emailViolation).isTrue();
    }

    @Test
    void whenPasswordIsInvalid_constraintViolation() {
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("short"); // too short, no digit

        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean passwordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertThat(passwordViolation).isTrue();
    }

    @Test
    void whenUsernameIsNull_constraintViolation() {
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setUsername(null); // null username
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");

        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean usernameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        assertThat(usernameViolation).isTrue();
    }
}
