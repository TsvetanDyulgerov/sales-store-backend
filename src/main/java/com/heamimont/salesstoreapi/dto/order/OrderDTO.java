package com.heamimont.salesstoreapi.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class OrderDTO {
    @NotNull(message = "Order date is required")
    protected LocalDate orderDate;

    @NotNull(message = "Total cost is required")
    @Positive(message = "Total cost must be positive")
    protected Double totalCost;
}
