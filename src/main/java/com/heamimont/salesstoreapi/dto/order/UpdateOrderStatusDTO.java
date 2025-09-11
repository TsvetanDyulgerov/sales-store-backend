package com.heamimont.salesstoreapi.dto.order;
import com.heamimont.salesstoreapi.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDTO {

    @Schema(description = "New status of the order (PENDING, IN_PROGRESS, DONE)", example = "DONE")
    @NotNull
    private OrderStatus status;
}
