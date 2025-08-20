package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponseDTO {
    private UUID id;
    private UserResponseDTO user;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private BigDecimal totalCost;
    private List<OrderProductResponseDTO> orderProducts;
}
