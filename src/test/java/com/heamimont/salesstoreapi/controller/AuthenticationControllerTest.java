package com.heamimont.salesstoreapi.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.auth.RegisterRequest;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.UserRepository;
import com.heamimont.salesstoreapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthenticationControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * This test verifies that a new user can register and then log in successfully.
     * It checks that the registration returns a token, which is then used to log in.
     * The login should return the same token as the registration.
     */
    @Test
    void testRegisterAndLoginNewUser_returnsToken() throws Exception {

        //Create a new user registration request
        RegisterRequest newUser = new RegisterRequest();
        newUser.setUsername("newuser");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        newUser.setEmail("test@mail.com");
        newUser.setPassword("password123");

        // Perform the registration request
        // Expect a 201 Created status and return the token in the response
        MvcResult registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the token from the response
        String registerToken = registerResponse.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(registerToken);
        registerToken = jsonNode.get("token").asText();

        // Verify that the user was created in the repository
        assertThat(userRepository.findByUsername("newuser")).isPresent();

        // Create a login request using the same credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("newuser");
        loginRequest.setPassword("password123");

        // Perform the login request with the registration token in the Authorization header
        MvcResult loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + registerToken)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the token from the login response
        String loginToken = loginResponse.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(loginToken);
        loginToken = jsonNode.get("token").asText();

        // Verify that the login token is the same as the registration token
        assertThat(loginToken).isEqualTo(registerToken);
    }


    /**
     * This test verifies that an existing user can log in successfully.
     * It checks that the login returns a valid token for the existing user.
     */
    @Test
    void testLoginExistingUser_returnsUserResponse() throws Exception {

        //Create an existing user in the database
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setEmail("email@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setRole(Role.USER);
        userRepository.save(existingUser);

        // Create a login request for the existing user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("existinguser");
        loginRequest.setPassword("password123");

        // Perform the login request
        MvcResult loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the response content
        String responseContent = loginResponse.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        // Verify that the token is not null or empty
        assertThat(token).isNotNull().isNotEmpty();

        //Validate token
        String usernameFromToken = jwtService.extractUsername(token);
        assertThat(usernameFromToken).isEqualTo("existinguser");
    }
}
