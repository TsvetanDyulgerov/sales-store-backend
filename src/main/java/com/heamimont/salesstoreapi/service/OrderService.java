package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderMapper;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.OrderStatus;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Creates a new order.
     * Automatically calculates the total cost of the order based on the products and their quantities.
     *
     * @param createOrderDTO the DTO containing order details
     * @return OrderResponseDTO containing the created order details
     * @throws ResourceCreationException if the order creation fails
     */
    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        try {
            Order order = orderMapper.toEntity(createOrderDTO);

            BigDecimal totalCost = new BigDecimal(0);

            for (OrderProduct op : order.getOrderProducts()) {
                BigDecimal lineTotal = op.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(op.getProductQuantity()));
                totalCost = totalCost.add(lineTotal);
            }

            order.setTotalCost(totalCost);
            Order savedOrder = orderRepository.save(order);
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
    public List<OrderResponseDTO> getOrdersByUsername(String username) {
        try {
            return orderRepository.findOrdersByUser_Username(username).stream()
                    .map(orderMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch orders for user [" + username + "]");
        }
    }

    /**
     * Retrieved all orders
     * @return List of OrderResponseDTO containing all orders
     * @throws ResourceNotFoundException if there are no orders found or an error occurs during retrieval
     */
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
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return OrderResponseDTO containing order details
     * @throws ResourceNotFoundException if the order with the given ID does not exist
     */
    public OrderResponseDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
