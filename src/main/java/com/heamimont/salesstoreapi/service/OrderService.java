package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.OrderStatus;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing orders.
 * This class provides methods to handle order-related operations.
 */

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findOrdersByUser_Username(username);
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> targetOrder = orderRepository.findById(orderId);

        if (targetOrder.isEmpty()) {
            return null;
        }

        Order existingOrder = targetOrder.get();
        existingOrder.setStatus(status);

        return orderRepository.save(existingOrder);
    }

    public List<Product> getProductsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getOrderProducts().stream()
                .map(OrderProduct::getProduct)
                .collect(Collectors.toList());
    }



}
