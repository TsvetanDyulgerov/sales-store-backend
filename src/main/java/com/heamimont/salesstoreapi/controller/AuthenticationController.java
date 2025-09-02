package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.auth.AuthResponse;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.service.AuthenticationService;
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
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * POST /api/auth/login
     * Authenticates a user and returns a JWT token.
     */
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
    @PostMapping("/register")
    @CrossOrigin(origins = "*") // allow requests from the frontend
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    @CrossOrigin(origins = "*") // allow requests from the frontend
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        // Authentication object is automatically populated by Spring Security after JWT verification
        User user = (User) authentication.getPrincipal();
        // Map User entity to DTO
        UserResponseDTO response = new UserResponseDTO(
                user.getUsername(),
                Objects.equals(user.getAuthorities().toString(), "[ROLE_ADMIN]") ? Role.ADMIN : Role.USER
        );

        return ResponseEntity.ok(response);
    }
}
