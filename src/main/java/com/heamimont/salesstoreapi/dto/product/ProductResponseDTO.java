package com.heamimont.salesstoreapi.dto.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductResponseDTO extends ProductDTO {
    private Long id;
    private BigDecimal actualPrice;
    private BigDecimal sellingPrice;
    private int availableQuantity;
    private Long version;
}
