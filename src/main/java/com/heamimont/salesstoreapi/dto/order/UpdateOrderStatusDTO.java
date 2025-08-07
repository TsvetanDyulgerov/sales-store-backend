package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateOrderStatusDTO extends OrderDTO {
    private OrderStatus status;
}
