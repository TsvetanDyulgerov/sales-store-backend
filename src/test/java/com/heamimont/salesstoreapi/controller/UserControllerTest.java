package com.heamimont.salesstoreapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 * Tests CRUD operations for User entity.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // clean DB before each test

        // Add a default admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setFirstName("Sys");
        admin.setLastName("Admin");
        admin.setEmail("adminemail@store.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO();
        newUser.setUsername("newuser");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        newUser.setEmail("exampleemail@domain.com");
        newUser.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        assertThat(userRepository.findByUsername("newuser")).isPresent();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // only admin exists
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User existing = userRepository.findByUsername("admin").orElseThrow();

        UpdateUserDTO updated = new UpdateUserDTO();
        updated.setUsername("updatedAdmin");
        updated.setPassword("password123");
        updated.setRole(Role.ADMIN);

        mockMvc.perform(put("/api/users/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedAdmin"));

        assertThat(userRepository.findByUsername("updatedAdmin")).isPresent();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_ShouldRemoveUser() throws Exception {
        User existing = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(delete("/api/users/{id}", existing.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(existing.getId())).isEmpty();
    }
}
