package com.heamimont.salesstoreapi.validation.user;

import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.model.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserValidationTest {

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
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenUsernameTooShort_constraintViolation() {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("ab"); // too short
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean usernameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        assertThat(usernameViolation).isTrue();
    }

    @Test
    void whenFirstNameTooShort_constraintViolation() {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("J"); // too short
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean firstNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
        assertThat(firstNameViolation).isTrue();
    }

    @Test
    void whenLastNameTooLong_constraintViolation() {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("A".repeat(40)); // too long
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("abc12345");
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean lastNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastName"));
        assertThat(lastNameViolation).isTrue();
    }

    @Test
    void whenEmailInvalid_constraintViolation() {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("invalid-email"); // invalid format
        userDTO.setPassword("abc12345");
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean emailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertThat(emailViolation).isTrue();
    }

    @Test
    void whenPasswordInvalid_constraintViolation() {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("short"); // too short, missing digit
        userDTO.setRole(Role.USER);

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(userDTO);
        assertThat(violations).isNotEmpty();

        boolean passwordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertThat(passwordViolation).isTrue();
    }
}
