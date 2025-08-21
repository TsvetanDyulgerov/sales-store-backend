package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.order.CreateOrderDTO;
import com.heamimont.salesstoreapi.mapper.OrderMapper;
import com.heamimont.salesstoreapi.dto.order.OrderResponseDTO;
import com.heamimont.salesstoreapi.exceptions.ResourceCreationException;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.*;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Order testOrder;
    private OrderResponseDTO testOrderResponseDTO;
    
    private UUID orderId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(orderId);
        testUser.setUsername("testuser");

        testOrder = new Order();
        testOrder.setUser(testUser);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setTotalCost(BigDecimal.valueOf(100));
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setOrderProducts(new ArrayList<>());

        testOrderResponseDTO = new OrderResponseDTO();
        testOrderResponseDTO.setId(testOrder.getId());
        testOrderResponseDTO.setOrderDate(testOrder.getOrderDate());
        testOrderResponseDTO.setTotalCost(testOrder.getTotalCost());
        testOrderResponseDTO.setStatus(testOrder.getStatus());
    }

    @Test
    void createOrder_success() {
        // Prepare DTO and mock behavior
        CreateOrderDTO createOrderDTO = mock(CreateOrderDTO.class);

        // Stub userRepository to find user by username
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Stub orderMapper.toEntity to convert DTO to Order entity
        when(orderMapper.toEntity(createOrderDTO)).thenReturn(testOrder);

        // Stub orderRepository.save to return the saved order
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Stub orderMapper.toDTO to convert Order entity to DTO
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderResponseDTO);

        // Add an OrderProduct with a product with price 50 and quantity 2 to test totalCost calculation
        Product product = new Product();
        product.setSellingPrice(BigDecimal.valueOf(50));
        OrderProduct op = new OrderProduct();
        op.setProduct(product);
        op.setProductQuantity(2);
        testOrder.setOrderProducts(List.of(op));

        OrderResponseDTO response = orderService.createOrder(createOrderDTO, "testuser");

        assertNotNull(response);
        assertEquals(testOrderResponseDTO.getId(), response.getId());

        // Verify totalCost is calculated correctly: 50 * 2 = 100
        assertEquals(BigDecimal.valueOf(100), testOrder.getTotalCost());

        verify(userRepository).findByUsername("testuser");
        verify(orderRepository).save(testOrder);
        verify(orderMapper).toDTO(testOrder);
    }

    @Test
    void createOrder_userNotFound_throwsResourceCreationException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        CreateOrderDTO createOrderDTO = mock(CreateOrderDTO.class);

        ResourceCreationException ex = assertThrows(ResourceCreationException.class,
                () -> orderService.createOrder(createOrderDTO, "unknown"));

        assertTrue(ex.getMessage().contains("Failed to create order"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrdersByUsername_success() {
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrdersByUser_Username("testuser")).thenReturn(orders);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderResponseDTO);

        List<OrderResponseDTO> result = orderService.getOrdersByUsername("testuser");

        assertEquals(1, result.size());
        assertEquals(testOrderResponseDTO, result.get(0));

        verify(orderRepository).findOrdersByUser_Username("testuser");
        verify(orderMapper).toDTO(testOrder);
    }

    @Test
    void getOrdersByUsername_failure_throwsResourceNotFoundException() {
        when(orderRepository.findOrdersByUser_Username("testuser")).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrdersByUsername("testuser"));

        assertTrue(ex.getMessage().contains("Failed to fetch orders"));
    }

    @Test
    void getAllOrders_success() {
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderResponseDTO);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(testOrderResponseDTO, result.get(0));
        verify(orderRepository).findAll();
        verify(orderMapper).toDTO(testOrder);
    }

    @Test
    void getAllOrders_failure_throwsResourceNotFoundException() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getAllOrders());

        assertTrue(ex.getMessage().contains("Failed to fetch all orders"));
    }

    @Test
    void updateOrderStatus_success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderResponseDTO);

        OrderResponseDTO result = orderService.updateOrderStatus(orderId, OrderStatus.DONE);

        assertNotNull(result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(testOrder);
        verify(orderMapper).toDTO(testOrder);
        assertEquals(OrderStatus.DONE, testOrder.getStatus());
    }

    @Test
    void updateOrderStatus_orderNotFound_throwsResourceNotFoundException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrderStatus(orderId, OrderStatus.DONE));

        assertTrue(ex.getMessage().contains("Order not found"));
    }

    @Test
    void getOrderById_success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderResponseDTO);

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(testOrderResponseDTO, result);
        verify(orderRepository).findById(orderId);
        verify(orderMapper).toDTO(testOrder);
    }

    @Test
    void getOrderById_notFound_throwsResourceNotFoundException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(orderId));

        assertTrue(ex.getMessage().contains("Order not found"));
    }
}
