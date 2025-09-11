package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.auth.AuthResponse;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * Controller for handling user authentication and registration.
 * It provides endpoints for logging in and registering users.
 */
@RestController
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * POST /api/auth/login
     * Authenticates a user and returns a JWT token.
     */
    @Operation(
            summary = "User Login",
            description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
    })
    @SecurityRequirements()
    @PostMapping("/login")
    @CrossOrigin(origins = "*") // allow requests from the frontend
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Registers a new user and returns a JWT token.
     */
    @Operation(
            summary = "User Registration",
            description = "Registers a new user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content)
    })
    @SecurityRequirements()
    @PostMapping("/register")
    @CrossOrigin(origins = "*") // allow requests from the frontend
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * GET /api/auth/me
     * Retrieves the currently authenticated user's details.
     * Requires a valid JWT token in the Authorization header.
     */
    @Operation(
            summary = "Get Current User",
            description = "Retrieves the currently authenticated user's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @GetMapping("/me")
    @CrossOrigin(origins = "*") // allow requests from the frontend
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        // Spring Security automatically populates an authentication object after JWT verification
        User user = (User) authentication.getPrincipal();
        // Map User entity to DTO
        UserResponseDTO response = new UserResponseDTO(
                user.getUsername(),
                Objects.equals(user.getAuthorities().toString(), "[ROLE_ADMIN]") ? Role.ADMIN : Role.USER
        );

        return ResponseEntity.ok(response);
    }
}
