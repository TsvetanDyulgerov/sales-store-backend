package com.heamimont.salesstoreapi.dto.product;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductDTO {

    @Size(max = 50, message = "Product name must not exceed 50 characters")
    private String name;

    @Size(max = 400, message = "Description must not exceed 400 characters")
    private String description;

    @Positive(message = "Actual price must be positive")
    private BigDecimal actualPrice;

    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    @PositiveOrZero(message = "Available quantity cannot be negative")
    private Integer availableQuantity;
}
