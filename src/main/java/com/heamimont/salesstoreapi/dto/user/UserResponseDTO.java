package com.heamimont.salesstoreapi.dto.user;
import com.heamimont.salesstoreapi.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponseDTO extends UserDTO {
    private Long id;
    private Role role;
}
