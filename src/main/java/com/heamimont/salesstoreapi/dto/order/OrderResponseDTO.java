package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponseDTO {
    @Schema(description = "Unique identifier of the order (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;
    private UserResponseDTO user;
    private OrderStatus status;
    @Schema(description = "Date and time when the order was placed", example = "2023-10-05T14:48:00")
    private LocalDateTime orderDate;
    private BigDecimal totalCost;
    private List<OrderProductResponseDTO> orderProducts;
}
