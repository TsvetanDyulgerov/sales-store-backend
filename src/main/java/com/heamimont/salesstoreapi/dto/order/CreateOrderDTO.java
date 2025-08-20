package com.heamimont.salesstoreapi.dto.order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {

    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private List<OrderProductDTO> orderProducts;
}
