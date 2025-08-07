package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UserMapper;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      UserMapper userMapper, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all users");
        }
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        try {
            if (userRepository.existsByUsername(createUserDTO.getUsername())) {
                throw new ResourceCreationException("Username already exists");
            }
            if (userRepository.existsByEmail(createUserDTO.getEmail())) {
                throw new ResourceCreationException("Email already exists");
            }

            User user = userMapper.toEntity(createUserDTO);
            user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
            User savedUser = userRepository.save(user);
            return userMapper.toDTO(savedUser);
        } catch (ResourceCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create user: " + e.getMessage());
        }
    }

    public UserResponseDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateUserDTO.getUsername() != null && 
            !updateUserDTO.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(updateUserDTO.getUsername())) {
            throw new ResourceCreationException("Username already exists");
        }

        if (updateUserDTO.getEmail() != null && 
            !updateUserDTO.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new ResourceCreationException("Email already exists");
        }

        userMapper.updateEntity(user, updateUserDTO);
        
        if (updateUserDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to delete user");
        }
    }
}
