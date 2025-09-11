package com.heamimont.salesstoreapi.controller;


import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.dto.order.UpdateOrderStatusDTO;
import com.heamimont.salesstoreapi.service.OrderService;
import com.heamimont.salesstoreapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * POST /api/orders
     * Create a new order for current user
     */
    @Operation(summary = "Create Order", description = "Create a new order for the current authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Order created successfully"),
            @ApiResponse(responseCode = "400" , description = "Invalid order data", content = @Content),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderDTO createOrderDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(createOrderDTO, username));
    }

    /**
     * POST /api/orders/admin/{username}
     * Create a new order for a specific user (admin only)
     */
    @Operation(summary = "Create Order for User", description = "Create a new order for a specific user (admin only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "Order created successfully"),
            @ApiResponse(responseCode = "400" , description = "Invalid order data", content = @Content),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access",content = @Content),
            @ApiResponse(responseCode = "404" , description = "User not found", content = @Content)
    })
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

    /**
     * GET /api/orders/{orderId}
     * Get order by ID (admin only)
     */
    @Operation(summary = "Get order by ID", description = "Retrieve order details by its ID (admin only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404" , description = "Order not found", content = @Content)
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * GET /api/orders/me
     * Get orders for current user
     */
    @Operation(summary = "Get Current User Orders", description = "Retrieve all orders for the current authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404" , description = "No orders found for the user", content = @Content)
    })
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getCurrentUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(orderService.getOrdersByUsername(userDetails.getUsername()));
    }

    /**
     * GET /api/orders
     * Get all orders (admin only)
     */
    @Operation(summary = "Get All Orders", description = "Retrieve all orders in the system (admin only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404" , description = "No orders found", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * PUT /api/orders/{orderId}/status
     * Update order status (admin only)
     */
    @Operation(summary = "Update Order Status", description = "Update the status of an order (admin only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400" , description = "Invalid status value", content = @Content),
            @ApiResponse(responseCode = "401" , description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404" , description = "Order not found", content = @Content)
    })
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusDTO updateOrderStatusDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, updateOrderStatusDTO.getStatus()));
    }
}
