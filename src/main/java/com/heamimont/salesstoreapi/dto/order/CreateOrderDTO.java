package com.heamimont.salesstoreapi.dto.order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateOrderDTO extends OrderDTO {

    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private List<OrderProductDTO> orderProducts;
}
