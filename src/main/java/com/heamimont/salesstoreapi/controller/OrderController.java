package com.heamimont.salesstoreapi.controller;


import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.dto.order.UpdateOrderStatusDTO;
import com.heamimont.salesstoreapi.service.OrderService;
import com.heamimont.salesstoreapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Create a new order for current user
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderDTO createOrderDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(createOrderDTO, username));
    }

    // Create a new order for current user
    @PostMapping("/admin/{username}")
    @PreAuthorize("hasRole( 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> createOrderAdmin(
            @Valid @RequestBody CreateOrderDTO createOrderDTO,
            @PathVariable String username) {

        if (userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderService.createOrder(createOrderDTO, username));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    // Get orders by order ID
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // Get current user's orders
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getCurrentUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(orderService.getOrdersByUsername(userDetails.getUsername()));
    }

    // Get all orders (admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Update order status by order ID
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusDTO updateOrderStatusDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, updateOrderStatusDTO.getStatus()));
    }
}
