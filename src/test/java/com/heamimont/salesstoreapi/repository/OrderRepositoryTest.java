package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setup() {
        // Create and save a User
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password"); // For test only, no encoding needed here
        testUser.setRole(Role.USER);
        userRepository.save(testUser);

        // Create and save a Product
        testProduct = new Product();
        testProduct.setName("Sample Product");
        testProduct.setDescription("A product for testing");
        testProduct.setActualPrice(BigDecimal.valueOf(10.00));
        testProduct.setSellingPrice(BigDecimal.valueOf(12.00));
        testProduct.setAvailableQuantity(100);
        productRepository.save(testProduct);
    }

    @Test
    void testSaveAndFindByUserUsername() {
        // Create an order
        Order order = new Order();
        order.setUser(testUser);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalCost(BigDecimal.valueOf(120));
        order.setStatus(OrderStatus.PENDING);

        // Add OrderProduct linking order and product
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(testProduct);
        orderProduct.setProductQuantity(10);

        // Important: set bidirectional relation
        order.getOrderProducts().add(orderProduct);

        // Save order (cascades orderProducts)
        orderRepository.save(order);

        // Fetch orders by username
        List<Order> orders = orderRepository.findOrdersByUser_Username("testuser");

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUser().getUsername()).isEqualTo("testuser");
        assertThat(orders.get(0).getOrderProducts()).hasSize(1);
        assertThat(orders.get(0).getOrderProducts().get(0).getProduct().getName()).isEqualTo("Sample Product");
    }
}
