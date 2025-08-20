package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderSpecificationsTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User user1;
    private User user2;
    private Product product1;
    private Product product2;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "alice", "Alice", "Smith", "alice@example.com", "password", Role.USER);
        user2 = new User(null, "bob", "Bob", "Johnson", "bob@example.com", "password", Role.USER);
        userRepository.saveAll(List.of(user1, user2));

        product1 = new Product(null, "Laptop", "High-end laptop", new BigDecimal("1000"), new BigDecimal("1200"), 10);
        product2 = new Product(null, "Mouse", "Wireless mouse", new BigDecimal("20"), new BigDecimal("25"), 50);
        productRepository.saveAll(List.of(product1, product2));

        order1 = new Order();
        order1.setUser(user1);
        order1.setOrderDate(LocalDateTime.of(2023, 8, 1, 12, 0));
        order1.setStatus(OrderStatus.DONE);
        order1.setTotalCost(new BigDecimal("1225"));
        order1.getOrderProducts().add(new OrderProduct(order1, product1, 1));
        order1.getOrderProducts().add(new OrderProduct(order1, product2, 1));
        orderRepository.save(order1);

        order2 = new Order();
        order2.setUser(user2);
        order2.setOrderDate(LocalDateTime.of(2023, 8, 15, 12 , 0));
        order2.setStatus(OrderStatus.PENDING);
        order2.setTotalCost(new BigDecimal("1000"));
        order2.getOrderProducts().add(new OrderProduct(order2, product1, 1));
        orderRepository.save(order2);
    }

    @Test
    void testHasProductName() {
        Specification<Order> spec = OrderSpecifications.hasProductName("laptop");
        List<Order> results = orderRepository.findAll(spec);
        assertThat(results).containsExactlyInAnyOrder(order1, order2);

        spec = OrderSpecifications.hasProductName("mouse");
        results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order1);
    }

    @Test
    void testHasUsername() {
        Specification<Order> spec = OrderSpecifications.hasUsername("alice");
        List<Order> results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order1);

        spec = OrderSpecifications.hasUsername("bob");
        results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order2);
    }

    @Test
    void testOrderDateAfter() {
        Specification<Order> spec = OrderSpecifications.orderDateAfter(LocalDate.of(2023, 8, 10));
        List<Order> results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order2);
    }

    @Test
    void testOrderDateBefore() {
        Specification<Order> spec = OrderSpecifications.orderDateBefore(LocalDate.of(2023, 8, 10));
        List<Order> results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order1);
    }

    @Test
    void testCombinedSpecifications() {
        Specification<Order> spec = OrderSpecifications.hasProductName("laptop")
                .and(OrderSpecifications.hasUsername("alice"))
                .and(OrderSpecifications.orderDateAfter(LocalDate.of(2023, 7, 31)))
                .and(OrderSpecifications.orderDateBefore(LocalDate.of(2023, 8, 2)));

        List<Order> results = orderRepository.findAll(spec);
        assertThat(results).containsExactly(order1);
    }

}
