package com.heamimont.salesstoreapi.dto.user;

import com.heamimont.salesstoreapi.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @Size(min = 3, max = 50)
    private String username;

    @Size(max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;

    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long, contain at least 1 letter and 1 number")
    private String password;

    private Role role;
}
