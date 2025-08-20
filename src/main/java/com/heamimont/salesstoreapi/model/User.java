package com.heamimont.salesstoreapi.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/*
 * User entity representing the users of the system.
 */

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Username cannot be null")
    @Column (nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 30)
    private String firstName;

    @Column(nullable = false, length = 30)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message="Email cannot be blank")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Column(nullable = false, length = 100) // Password validation is in CreateUserDTO
    private String password;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;



}
