package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.dto.order.OrderDTO;
import com.heamimont.salesstoreapi.dto.user.UserResponseDTO;
import com.heamimont.salesstoreapi.model.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderResponseDTO extends OrderDTO {
    private Long id;
    private UserResponseDTO user;
    private OrderStatus status;
    private List<OrderProductResponseDTO> orderProducts;
}
