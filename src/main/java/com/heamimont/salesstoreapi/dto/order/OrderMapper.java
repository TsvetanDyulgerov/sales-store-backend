package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.dto.user.UserMapper;
import com.heamimont.salesstoreapi.exceptions.ResourceNotFoundException;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.repository.ProductRepository;
import com.heamimont.salesstoreapi.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
public class OrderMapper {
    private final ProductRepository productRepository;
    private final UserMapper userMapper;

    public OrderMapper(UserRepository userRepository, 
                      ProductRepository productRepository,
                      UserMapper userMapper) {
        this.productRepository = productRepository;
        this.userMapper = userMapper;
    }

    public Order toEntity(CreateOrderDTO dto) {
        Order order = new Order();
        order.setOrderDate(dto.getOrderDate());
        order.setTotalCost(dto.getTotalCost());
        order.setOrderProducts(new ArrayList<>());
        
        if (dto.getOrderProducts() != null) {
            dto.getOrderProducts().forEach(item -> {
                OrderProduct orderProduct = new OrderProduct();
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                orderProduct.setProduct(product);
                orderProduct.setOrder(order);
                orderProduct.setProductQuantity(item.getProductQuantity());
                order.getOrderProducts().add(orderProduct);
            });
        }
        
        return order;
    }

    public OrderResponseDTO toDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUser(userMapper.toDTO(order.getUser()));
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalCost(order.getTotalCost());
        dto.setStatus(order.getStatus());
        
        if (order.getOrderProducts() != null) {
            dto.setOrderProducts(order.getOrderProducts().stream()
                    .map(this::toOrderProductDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private OrderProductResponseDTO toOrderProductDTO(OrderProduct orderProduct) {
        OrderProductResponseDTO dto = new OrderProductResponseDTO();
        dto.setProductId(orderProduct.getProduct().getId());
        dto.setProductName(orderProduct.getProduct().getName());
        dto.setProductQuantity(orderProduct.getProductQuantity());
        dto.setProductPrice(orderProduct.getProduct().getSellingPrice().doubleValue());
        return dto;
    }
}
