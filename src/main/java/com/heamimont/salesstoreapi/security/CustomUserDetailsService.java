package com.heamimont.salesstoreapi.security;

import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation for loading user details from the database.
 * This service is used by Spring Security to authenticate users.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by username.
     * This method is called by Spring Security during authentication.
     *
     * @param username the username of the user to load
     * @return UserDetails containing user information
     * @throws UsernameNotFoundException if the user with the given username does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Return UserDetails object with user information
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRole().getAuthorities()
        );
    }
}
