package com.heamimont.salesstoreapi.dto.order;
import lombok.Data;

@Data
public class OrderProductResponseDTO {
    private Long productId;
    private String productName;
    private Integer productQuantity;
    private Double productPrice;
}
