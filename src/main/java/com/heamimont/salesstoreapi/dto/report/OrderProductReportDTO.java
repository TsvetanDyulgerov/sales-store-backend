package com.heamimont.salesstoreapi.dto.report;

import lombok.Data;

@Data
public class OrderProductReportDTO {
    private String productName;
    private int quantity;

    public OrderProductReportDTO() {}

    public OrderProductReportDTO(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}
