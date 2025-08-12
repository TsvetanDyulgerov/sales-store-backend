package com.heamimont.salesstoreapi.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.heamimont.salesstoreapi.dto.auth.AuthResponse;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UserMapper;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class AuthenticationServiceTest {

    private UserService userService;
    private UserMapper userMapper;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        userMapper = mock(UserMapper.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsService.class);

        authenticationService = new AuthenticationService(userService, userMapper, jwtService, authenticationManager, userDetailsService);
    }

    @Test
    void register_ShouldReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("test@example.com");

        CreateUserDTO createUserDTO = new CreateUserDTO();
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername("testuser");

        UserDetails userDetails = mock(UserDetails.class);

        when(userMapper.fromRegisterRequest(request)).thenReturn(createUserDTO);
        when(userService.createUser(createUserDTO)).thenReturn(userResponseDTO);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token123");

        AuthResponse response = authenticationService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token123");

        verify(userMapper).fromRegisterRequest(request);
        verify(userService).createUser(createUserDTO);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void authenticate_ShouldReturnToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token123");

        AuthResponse response = authenticationService.authenticate(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token123");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).generateToken(userDetails);
    }

}
