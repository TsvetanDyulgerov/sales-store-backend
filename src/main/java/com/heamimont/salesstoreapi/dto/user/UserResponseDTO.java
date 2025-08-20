package com.heamimont.salesstoreapi.dto.user;
import com.heamimont.salesstoreapi.model.Role;
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
}
