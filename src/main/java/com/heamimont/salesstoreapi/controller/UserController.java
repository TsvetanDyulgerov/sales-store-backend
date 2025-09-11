package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.exceptions.BadRequestException;
import com.heamimont.salesstoreapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/users")
@PreAuthorize("hasRole('ADMIN')")  // All endpoints require ADMIN role
@Tag(name = "Users", description = "[ADMIN] Endpoints for managing users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users
     * Retrieves a list of all users.
     */
    @Operation(summary = "Get All Users", description = "Retrieve a list of all users. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * GET /api/users/{id}
     * Retrieves a user by their ID.
     */
    @Operation(summary = "Get User by ID", description = "Retrieve a user by their ID. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return ResponseEntity.ok(userService.getUserById(uuid));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    /**
     * GET /api/users/username/{username}
     * Retrieves a user by their username.
     */
    @Operation(summary = "Get User by Username", description = "Retrieve a user by their username. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * POST /api/users
     * Creates a new user.
     */
    @Operation(summary = "Create User", description = "Create a new user. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(createUserDTO));
    }

    /**
     * PUT /api/users/{id}
     * Updates an existing user by their ID.
     */
    @Operation(summary = "Update User", description = "Update an existing user by their ID. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDTO));
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user by their ID.
     */
    @Operation(summary = "Delete User", description = "Delete a user by their ID. Accessible only by admin users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
