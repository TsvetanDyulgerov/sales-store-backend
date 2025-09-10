package com.heamimont.salesstoreapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.auth.LoginRequest;
import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderProductDTO;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.dto.product.CreateProductDTO;
import com.heamimont.salesstoreapi.dto.product.ProductResponseDTO;
import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.dto.user.CreateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UpdateUserDTO;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SalesStoreApi application.
 * This test class simulates a full application flow including:
 * user creation, authentication, product management, and order processing and report generation via REST API endpoints.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // to allow @BeforeAll to be used
@ActiveProfiles("test")
public class SalesStoreApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;

    @Autowired ProductRepository productRepository;

    @Autowired UserRepository userRepository;

    @Autowired OrderRepository orderRepository;

    @Test
    void exampleFullAppFlow() throws Exception {
        String adminToken = createAdmin(); // Create an admin user manually and get the token
        UserResponseDTO createdUser = createNewUser("manager",adminToken); // Create a new user (manager) using the admin token
        UserResponseDTO updatedUser= updateNewUser(createdUser, adminToken); // Update the new user to escalate their privileges to admin

        // Verify the new user was created and updated correctly
        assertThat(userRepository.findByUsername(createdUser.getUsername())).isPresent();
        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);

        // -- Log in as the new admin user to get a token for further actions --
        String managerToken = login("manager", "userpass123");

        // -- Create a new product using the new manager user --
        CreateProductDTO createdProduct = new CreateProductDTO();
        createdProduct.setName("Test Product");
        createdProduct.setDescription("This is a test product");
        createdProduct.setActualPrice(BigDecimal.valueOf(100.00));
        createdProduct.setSellingPrice(BigDecimal.valueOf(120.00));
        createdProduct.setAvailableQuantity(50);
        ProductResponseDTO productResponse = createNewProduct(createdProduct, managerToken);

        // Verify the product was created successfully
        assertThat(productRepository.findById(productResponse.getId())).isPresent();

        // -- Create a new customer user through the API --
        CreateUserDTO createdUserDTO = new CreateUserDTO();
        createdUserDTO.setUsername("customer");
        createdUserDTO.setFirstName("Jane");
        createdUserDTO.setLastName("Doe");
        createdUserDTO.setEmail("customer@mail.com");
        createdUserDTO.setPassword("customerpass123");

        String customerToken = registerUser(createdUserDTO); // Register the new customer user

        // -- Create a new order for the customer using the product created by the manager user --
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProductId(productResponse.getId());
        orderProductDTO.setProductQuantity(2);

        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setOrderProducts(List.of(orderProductDTO));
        OrderResponseDTO orderResponse = createNewOrder(createOrderDTO, customerToken); // Create a new order

        // Verify the order was created successfully
         assertThat(orderRepository.findById(orderResponse.getId())).isPresent();

        // -- Update the order status to DONE --
        OrderResponseDTO updatedOrder = updateOrder(orderResponse, managerToken, OrderStatus.DONE);
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.DONE);

        // -- Get all users using the new manager user token --
        List<UserResponseDTO> users = getAllUsers(managerToken);

        // Verify the number of users retrieved matches the number in the repository
        List<User> allUsers = userRepository.findAll();
        assertThat(users.size()).isEqualTo(allUsers.size());

        // -- Generate a report of orders within a date range (last 7 days)

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        List<OrderReportDTO> orderReports = getOrderReportForPeriod(managerToken, startDate, endDate);

        // -- Verify the report contains the created order
        List<Order> allOrders = orderRepository.findAll();
        assertThat(orderReports.size()).isEqualTo(allOrders.size());
        assertThat(orderReports.stream().anyMatch(r -> r.getOrderId().equals(orderResponse.getId()))).isTrue();

        // -- Delete the customer user using the manager token --
        UserResponseDTO userResponseDTO = getUserByUsername(managerToken, "customer");
        deleteUser(userResponseDTO.getId(), managerToken);

        // Verify the user was deleted successfully
        assertThat(userRepository.findByUsername("customer")).isNotPresent();

    }

    private String login(String username, String password) throws Exception {
        // Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // Perform the login request
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expecting 200 OK for successful login
                .andReturn();

        // Extract the token from the response
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    private String registerUser(CreateUserDTO createUserDTO) throws Exception {
        // Perform the registration request
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated()) // Expecting 201 Created for successful registration
                .andReturn();

        // Extract the token from the response
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    private String createAdmin() throws Exception {
        // Create an admin user. This has to be done manually as a user with admin privileges cannot be created
        // through the API without an existing admin user.
        User user = new User();
        user.setUsername("sysadmin");
        user.setFirstName("System");
        user.setLastName("Administrator");
        user.setEmail("sysadmin@example.com");
        user.setPassword(passwordEncoder.encode("adminpass123"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        // Now log in with the admin user to get a token
        // This is necessary to perform actions that require admin privileges
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sysadmin");
        loginRequest.setPassword("adminpass123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expecting 200 OK for successful login
                .andReturn();

        // Extract the token from the login response
        String body = result.getResponse().getContentAsString();

        return objectMapper.readTree(body).get("token").asText();
    }


    private UserResponseDTO createNewUser(String username, String authToken) throws Exception {

        // --- Create a new user using the admin token ---

        CreateUserDTO newUser = new CreateUserDTO();
        newUser.setUsername(username);
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setEmail("user@mail.com");
        newUser.setPassword("userpass123");

        MvcResult result = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated()) // Expecting 201 Created for successful user creation
                .andReturn();

        // Extract the created user from the response
        String userResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(userResponseBody, UserResponseDTO.class);
    }

    private List<UserResponseDTO> getAllUsers(String authToken) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk()) // Expecting 200 OK for successful retrieval
                .andReturn();

        // Extract the list of users from the response
        String usersResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(usersResponseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserResponseDTO.class));
    }

    private UserResponseDTO updateNewUser(UserResponseDTO createdUser, String adminToken) throws Exception {
        // -- Update the new user (manager) to escalate to admin privileges using existing admin --

        UpdateUserDTO updateUser = new UpdateUserDTO();
        updateUser.setRole(Role.ADMIN);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", createdUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk()) // Expecting 200 OK for successful update
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn(); // Verify the role was updated to ADMIN

        String body = response.getResponse().getContentAsString();
        return objectMapper.readValue(body, UserResponseDTO.class);
    }

    private UserResponseDTO getUserByUsername(String authToken, String username) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/{username}", username)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk()) // Expecting 200 OK for successful retrieval
                .andReturn();

        // Extract the user from the response
        String userResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(userResponseBody, UserResponseDTO.class);
    }

    private ProductResponseDTO createNewProduct(CreateProductDTO createProductDTO, String authToken) throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductDTO)))
                .andExpect(status().isCreated()) // Expecting 201 Created for successful product creation
                .andReturn();

        // Extract the created product from the response
        String productResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(productResponseBody, ProductResponseDTO.class);
    }

    private OrderResponseDTO createNewOrder(CreateOrderDTO createOrderDTO, String authToken) throws Exception {
        // -- Create a new order for the customer using the product created by the manager user --

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDTO)))
                .andExpect(status().isCreated()) // Expecting 201 Created for successful order creation
                .andReturn();

        // Extract the created order from the response
        String orderResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(orderResponseBody, OrderResponseDTO.class);
    }

    private OrderResponseDTO updateOrder(OrderResponseDTO orderResponseDTO, String authToken, OrderStatus status) throws Exception {

        // -- Update the order status --

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/{id}/status", orderResponseDTO.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + status.name() + "\"}"))
                .andExpect(status().isOk()) // Expecting 200 OK for successful update
                .andExpect(jsonPath("$.status").value(status.name())) // Verify the status was updated
                .andReturn();

        // Extract the updated order from the response
        String orderResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(orderResponseBody, OrderResponseDTO.class);
    }

    private List<OrderReportDTO> getOrderReportForPeriod(String authToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/reports")
                        .header("Authorization", "Bearer " + authToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk()) // Expecting 200 OK for successful retrieval
                .andReturn();

        // Extract the list of order reports from the response
        String reportResponseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(reportResponseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderReportDTO.class));

    }

    private void deleteUser(UUID userId, String authToken) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent()); // Expecting 200 OK for successful deletion
    }
}
