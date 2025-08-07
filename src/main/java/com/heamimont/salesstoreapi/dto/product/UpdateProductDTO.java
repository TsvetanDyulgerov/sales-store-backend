package com.heamimont.salesstoreapi.dto.product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateProductDTO extends ProductDTO {
    @Positive(message = "Actual price must be positive")
    private BigDecimal actualPrice;

    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;
}
