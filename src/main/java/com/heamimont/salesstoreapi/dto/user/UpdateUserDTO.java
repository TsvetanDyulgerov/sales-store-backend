package com.heamimont.salesstoreapi.dto.user;

import com.heamimont.salesstoreapi.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for update user details requests.
 * Exists because UserDTO does not contain password field and requires all fields to be present.
 * This DTO allows partial updates of user details.
 */


@Data
public class UpdateUserDTO {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    private String firstName;

    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    private String lastName;

    @Schema(description = "User's email address", example = "examplemail@mail.com")
    @Email
    private String email;

    @Schema(description = "Password must contain at least 1 letter, 1 number, and be at least 8 characters long", example = "Password123")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long, contain at least 1 letter and 1 number")
    private String password;

    private Role role;
}
