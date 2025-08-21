package com.heamimont.salesstoreapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderProductDTO;
import com.heamimont.salesstoreapi.dto.order.UpdateOrderStatusDTO;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Seed ADMIN user
        User admin = new User();
        admin.setUsername("admin");
        admin.setFirstName("Sys");
        admin.setLastName("Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Seed regular USER
        User customer = new User();
        customer.setUsername("customer");
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("customer@example.com");
        customer.setPassword("password123");
        customer.setRole(Role.USER);
        userRepository.save(customer);

        // Seed a Product used in orders
        product = new Product();
        product.setName("Laptop");
        product.setDescription("laptop for testing");
        product.setActualPrice(BigDecimal.valueOf(900));
        product.setSellingPrice(BigDecimal.valueOf(1200));
        product.setAvailableQuantity(100);
        product = productRepository.save(product);
    }

    // Helper to build a minimal valid CreateOrderDTO for the seeded product
    private CreateOrderDTO validCreateOrderDTO(int quantity) {
        OrderProductDTO op = new OrderProductDTO();
        op.setProductId(product.getId());
        op.setProductQuantity(quantity);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setOrderProducts(Collections.singletonList(op));
        return dto;
    }

    private String createOrderAndReturnId(CreateOrderDTO dto) throws Exception {
        String body = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        return root.get("id").asText();
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void createOrder_asUser_ShouldReturnCreatedAndPersist() throws Exception {
        long before = orderRepository.count();

        CreateOrderDTO dto = validCreateOrderDTO(2);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        assertThat(orderRepository.count()).isEqualTo(before + 1);
        Order saved = orderRepository.findAll().get(0);
        assertThat(saved.getUser().getUsername()).isEqualTo("customer");
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void getCurrentUserOrders_asUser_ShouldReturnOnlyOwnOrders() throws Exception {
        CreateOrderDTO dto = validCreateOrderDTO(1);
        createOrderAndReturnId(dto);

        mockMvc.perform(get("/api/orders/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllOrders_asAdmin_ShouldReturnList() throws Exception {
        CreateOrderDTO dto = validCreateOrderDTO(3);
        createOrderAndReturnId(dto);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void getAllOrders_asUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anyEndpoint_withoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateOrderStatus_asAdmin_ShouldUpdateAndReturnOk() throws Exception {
        // Create an order first (as admin; service will attach to "admin" user)
        String orderId = createOrderAndReturnId(validCreateOrderDTO(2));

        UpdateOrderStatusDTO body = new UpdateOrderStatusDTO();
        body.setStatus(OrderStatus.DONE);

        mockMvc.perform(put("/api/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        Order updated = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.DONE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateOrderStatus_nonExistingOrder_ShouldReturn404() throws Exception {
        UpdateOrderStatusDTO body = new UpdateOrderStatusDTO();
        body.setStatus(OrderStatus.DONE);

        mockMvc.perform(put("/api/orders/{id}/status", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOrderById_asAdmin_ShouldReturnOrder() throws Exception {
        String orderId = createOrderAndReturnId(validCreateOrderDTO(1));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOrderById_nonExisting_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ---------- Validation error scenarios ----------

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void createOrder_missingFields_ShouldReturn400() throws Exception {
        // No date, no totalCost, no products
        CreateOrderDTO dto = new CreateOrderDTO();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void createOrder_emptyProducts_ShouldReturn400() throws Exception {
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setOrderProducts(Collections.emptyList()); // violates @NotEmpty

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void createOrder_invalidProductQuantity_ShouldReturn400() throws Exception {
        OrderProductDTO op = new OrderProductDTO();
        op.setProductId(product.getId());
        op.setProductQuantity(0); // violates @Min(1)

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setOrderProducts(Collections.singletonList(op));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
