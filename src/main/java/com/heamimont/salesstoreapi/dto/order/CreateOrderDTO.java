package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateOrderDTO extends OrderDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private List<OrderProductDTO> orderProducts;
}
