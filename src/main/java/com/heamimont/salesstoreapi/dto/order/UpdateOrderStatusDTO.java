package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDTO {

    @NotNull
    private OrderStatus status;
}
