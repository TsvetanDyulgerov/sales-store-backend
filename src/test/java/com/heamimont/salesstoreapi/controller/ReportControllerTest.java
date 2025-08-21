package com.heamimont.salesstoreapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderProductDTO;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import com.heamimont.salesstoreapi.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private OrderResponseDTO orderResponseDTO1; // John’s order
    private OrderResponseDTO orderResponseDTO2; // Foo’s order

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Seed user John
        User john = new User();
        john.setUsername("john");
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setEmail("john@doe.com");
        john.setPassword(passwordEncoder.encode("password123"));
        john.setRole(Role.USER);
        userRepository.save(john);

        // Seed user Foo
        User foo = new User();
        foo.setUsername("foobar");
        foo.setFirstName("Foo");
        foo.setLastName("Bar");
        foo.setEmail("foobar@doe.com");
        foo.setPassword(passwordEncoder.encode("password123"));
        foo.setRole(Role.USER);
        userRepository.save(foo);

        // Seed products
        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setSellingPrice(BigDecimal.valueOf(1000.0));
        laptop.setActualPrice(BigDecimal.valueOf(900.0));
        productRepository.save(laptop);

        Product phone = new Product();
        phone.setName("Phone");
        phone.setSellingPrice(BigDecimal.valueOf(500));
        phone.setActualPrice(BigDecimal.valueOf(400));
        productRepository.save(phone);

        // Seed John’s order (Laptop)
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setProductId(laptop.getId());
        orderProductDTO.setProductQuantity(1);
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setOrderProducts(List.of(orderProductDTO));
        orderResponseDTO1 = orderService.createOrder(createOrderDTO, john.getUsername());

        // Seed Foo’s order (2 Phones)
        OrderProductDTO orderProductDTO2 = new OrderProductDTO();
        orderProductDTO2.setProductId(phone.getId());
        orderProductDTO2.setProductQuantity(2);
        CreateOrderDTO createOrderDTO2 = new CreateOrderDTO();
        createOrderDTO2.setOrderProducts(List.of(orderProductDTO2));
        orderResponseDTO2 = orderService.createOrder(createOrderDTO2, foo.getUsername());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return all reports when no filters are provided")
    void getReports_withoutFilters_ShouldReturnAllReports() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // At least 2 results
                .andExpect(jsonPath("$.length()").value(2))
                // John’s order
                .andExpect(jsonPath("$[?(@.orderId == '%s')].products[0].productName",
                        orderResponseDTO1.getId().toString()).value("Laptop"))
                .andExpect(jsonPath("$[?(@.orderId == '%s')].userFullName",
                        orderResponseDTO1.getId().toString()).value("John Doe"))
                // Foo’s order
                .andExpect(jsonPath("$[?(@.orderId == '%s')].products[0].productName",
                        orderResponseDTO2.getId().toString()).value("Phone"))
                .andExpect(jsonPath("$[?(@.orderId == '%s')].userFullName",
                        orderResponseDTO2.getId().toString()).value("Foo Bar"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should filter only John's Laptop order")
    void getReports_withFilters_ShouldReturnOnlyJohnsOrder() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .param("productName", "Laptop")
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(orderResponseDTO1.getId().toString()))
                .andExpect(jsonPath("$[0].products[0].productName").value("Laptop"))
                .andExpect(jsonPath("$[0].userFullName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should filter only Foo's Phone order")
    void getReports_withFilters_ShouldReturnOnlyFoosOrder() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .param("productName", "Phone")
                        .param("username", "foobar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(orderResponseDTO2.getId().toString()))
                .andExpect(jsonPath("$[0].products[0].productName").value("Phone"))
                .andExpect(jsonPath("$[0].userFullName").value("Foo Bar"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should filter by date range (both orders fall within)")
    void getReports_withDateFilters_ShouldReturnBoth() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .param("startDate", String.valueOf(LocalDateTime.now().minusDays(1)))
                        .param("endDate", String.valueOf(LocalDateTime.now().plusDays(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty array when filters don't match")
    void getReports_withUnmatchedFilters_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .param("productName", "NonexistentProduct"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid date format")
    void getReports_invalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .param("startDate", "not-a-date"))
                .andExpect(status().isBadRequest());
    }
}
