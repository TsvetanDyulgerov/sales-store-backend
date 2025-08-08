package com.heamimont.salesstoreapi.dto.report;

// DTO representing a product inside an order for the report
public class OrderProductReportDTO {
    private String productName;
    private int quantity;

    public OrderProductReportDTO() {}
    public OrderProductReportDTO(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
    // getters and setters
}
