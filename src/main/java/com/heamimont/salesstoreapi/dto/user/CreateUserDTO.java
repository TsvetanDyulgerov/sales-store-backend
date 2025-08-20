package com.heamimont.salesstoreapi.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    protected String username;

    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name must not exceed 30 characters")
    protected String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name must not exceed 30 characters")
    protected String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    protected String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
            message = "Password must be at least 8 characters long, contain at least 1 letter and 1 number")
    private String password;
}
