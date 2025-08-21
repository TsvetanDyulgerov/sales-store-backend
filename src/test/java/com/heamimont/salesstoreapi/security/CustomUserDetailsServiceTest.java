package com.heamimont.salesstoreapi.security;

import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        String username = "testuser";
        String password = "encodedpassword";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setRole(Role.ADMIN); // Use actual enum

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

        // Check that authorities match Role.ADMIN
        Set<String> expectedAuthorities = Role.ADMIN.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Set<String> actualAuthorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(expectedAuthorities, actualAuthorities);

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertTrue(ex.getMessage().contains(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}
