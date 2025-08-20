package com.heamimont.salesstoreapi.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal actualPrice;
    private BigDecimal sellingPrice;
    private int availableQuantity;
    private Long version;
}
