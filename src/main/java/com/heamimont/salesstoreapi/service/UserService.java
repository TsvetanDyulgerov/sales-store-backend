package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.mapper.UserMapper;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service class for managing users in the sales store API.
 * Provides methods to create, read, update, and delete users.
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(UserRepository userRepository, 
                      UserMapper userMapper, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return List of UserResponseDTO containing all users
     * @throws ResourceNotFoundException if an error occurs while fetching users
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all users");
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return UserResponseDTO containing the user details
     * @throws ResourceNotFoundException if the user with the given ID does not exist
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return UserResponseDTO containing the user details
     * @throws ResourceNotFoundException if the user with the given username does not exist
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Creates a new user.
     *
     * @param createUserDTO the DTO containing user details
     * @return UserResponseDTO containing the created user details
     * @throws ResourceCreationException if the user creation fails due to existing username or email
     */
    @Transactional
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        try {
            if (userRepository.existsByUsernameIgnoreCase(createUserDTO.getUsername())) {
                throw new ResourceCreationException("Username already exists");
            }
            if (userRepository.existsByEmailIgnoreCase(createUserDTO.getEmail())) {
                throw new ResourceCreationException("Email already exists");
            }

            User user = userMapper.toEntity(createUserDTO);
            user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
            user.setRole(Role.USER); // DEFAULT_ROLE
            User savedUser = userRepository.save(user);
            logger.info("[User Creation]: User ({}, {}) created successfully", savedUser.getId(), savedUser.getUsername());
            return userMapper.toDTO(savedUser);

        } catch (ResourceCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Updates an existing user by their ID.
     *
     * @param id the ID of the user to update
     * @param dto the DTO containing updated user details
     * @return UserResponseDTO containing the updated user details
     * @throws ResourceNotFoundException if the user with the given ID does not exist
     * @throws ResourceCreationException if the username or email already exists when updating
     */
    @Transactional
    public UserResponseDTO updateUser(UUID id, @Valid UpdateUserDTO dto) {
        // Find the existing user or throw an exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check username uniqueness only if it's being updated
        boolean isUsernameUpdate = dto.getUsername() != null &&
                              !dto.getUsername().equals(user.getUsername());
        if (isUsernameUpdate && userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new ResourceCreationException("Username already exists");
        }

        // Check email uniqueness only if it's being updated
        boolean isEmailUpdate = dto.getEmail() != null &&
                           !dto.getEmail().equals(user.getEmail());
        if (isEmailUpdate && userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ResourceCreationException("Email already exists");
        }

        // Update user fields using mapper
        userMapper.updateEntity(user, dto);

        // Update password if provided (with encryption)
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Save and return updated user
        User updatedUser = userRepository.save(user);
        logger.info("[User Update]: User ({}, {}) updated successfully", updatedUser.getId(), updatedUser.getUsername());
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if the user with the given ID does not exist
     */
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        try {
            userRepository.deleteById(id);
            logger.info("[User Deletion]: User ({}) deleted successfully", id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to delete user");
        }
    }
}
