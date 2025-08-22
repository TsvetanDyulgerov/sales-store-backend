package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.mapper.OrderMapper;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.OrderStatus;
import com.heamimont.salesstoreapi.model.User;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing orders.
 * Provides methods to create, retrieve, and update orders.
 */

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new order.
     * Automatically calculates the total cost of the order based on the products and their quantities.
     *
     * @param createOrderDTO the DTO containing order details
     * @return OrderResponseDTO containing the created order details
     * @throws ResourceCreationException if the order creation fails
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO, String username) {
        try {
            // Fetch user entity by username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Order order = orderMapper.toEntity(createOrderDTO);
            order.setUser(user);

            BigDecimal totalCost = new BigDecimal(0);

            for (OrderProduct op : order.getOrderProducts()) {
                BigDecimal lineTotal = op.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(op.getProductQuantity()));
                totalCost = totalCost.add(lineTotal);
            }

            order.setTotalCost(totalCost);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);
            Order savedOrder = orderRepository.save(order);
            logger.info("[Order Creation] Order ({}, by {}) created successfully", savedOrder.getId(), savedOrder.getUser().getUsername());
            return orderMapper.toDTO(savedOrder);
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Retrieves orders by username.
     *
     * @param username the username of the user whose orders are to be retrieved
     * @return List of OrderResponseDTO containing orders for the specified user
     * @throws ResourceNotFoundException if no orders are found for the given username or an error occurs during retrieval
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUsername(String username) {
        try {
            List<Order> orders = orderRepository.findOrdersByUser_Username(username).orElse(null);

            if (orders == null || orders.isEmpty()) {
                throw new ResourceNotFoundException("No orders found for user [" + username + "]");
            } else {
                return orders.stream()
                        .map(orderMapper::toDTO)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch orders for user [" + username + "]");
        }
    }

    /**
     * Retrieved all orders
     * @return List of OrderResponseDTO containing all orders
     * @throws ResourceNotFoundException if there are no orders found or an error occurs during retrieval
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        try {
            return orderRepository.findAll().stream()
                    .map(orderMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch all orders");
        }
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the ID of the order to update
     * @param status the new status to set for the order
     * @return OrderResponseDTO containing the updated order details
     * @throws ResourceNotFoundException if the order with the given ID does not exist
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        logger.info("[Order Update] Order ({}) status updated to {}", updatedOrder.getId(), status);
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return OrderResponseDTO containing order details
     * @throws ResourceNotFoundException if the order with the given ID does not exist
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
