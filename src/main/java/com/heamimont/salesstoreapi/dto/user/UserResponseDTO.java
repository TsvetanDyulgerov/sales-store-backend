package com.heamimont.salesstoreapi.dto.user;
import com.heamimont.salesstoreapi.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    public UserResponseDTO() {}

    public UserResponseDTO(String username, Role role) {
        this.username = username;
        this.role = role;
    }
}
