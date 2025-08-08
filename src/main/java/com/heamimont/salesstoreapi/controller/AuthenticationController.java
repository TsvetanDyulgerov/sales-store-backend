package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.auth.AuthResponse;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.service.AuthenticationService;
import com.heamimont.salesstoreapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
    }

    /**
     * POST /api/auth/login
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Registers a new user and returns a JWT token.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }
}
