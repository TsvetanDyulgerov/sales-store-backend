package com.heamimont.salesstoreapi.dto.user;

import com.heamimont.salesstoreapi.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateUserDTO extends UserDTO {
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
            message = "Password must be at least 8 characters long, contain at least 1 letter and 1 number")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}
