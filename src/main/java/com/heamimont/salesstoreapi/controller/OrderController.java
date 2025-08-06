package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;


    // POST /orders - User access
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // GET /orders/user - User access
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getCurrentUserOrders(Principal principal) {
        String username = principal.getName(); // from authenticated user
        List<Order> orders = orderService.getOrdersByUsername(username);
        return ResponseEntity.ok(orders);}

    // GET /orders - Admin only
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // PUT /orders/{orderId}/status - Admin only
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@RequestBody Order order, @PathVariable Long orderId) {
        Order updated = orderService.updateOrderStatus(orderId, order.getStatus());

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

}
