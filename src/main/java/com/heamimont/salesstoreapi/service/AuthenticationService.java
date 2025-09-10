package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.auth.AuthResponse;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.exceptions.BadCredentialsException;
import com.heamimont.salesstoreapi.mapper.UserMapper;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user authentication and registration.
 * It uses UserService for user management and JwtService for JWT token generation.
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Handles user registration and JWT token issuance.
     * It maps the RegisterRequest to CreateUserDTO, creates the user,
     * and generates a JWT token for the newly registered user.
     * @param request RegisterRequest containing user details for registration
     * @return AuthResponse containing the JWT token
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Map RegisterRequest to CreateUserDTO
        CreateUserDTO createUserDTO = userMapper.fromRegisterRequest(request);

        // Create the user using existing logic
        UserResponseDTO savedUserDTO = userService.createUser(createUserDTO);

        // Load UserDetails for token generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUserDTO.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken);
    }

    /**
     * Authenticates a user using their username and password.
     * It uses the AuthenticationManager to validate credentials and
     * generates a JWT token if authentication is successful.
     * @param request LoginRequest containing username and password
     * @return AuthResponse containing the JWT token
     */
    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        // Validate credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Generate token
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwtToken = jwtService.generateToken(userDetails);

            return new AuthResponse(jwtToken);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password", ex);
        }
    }
}
