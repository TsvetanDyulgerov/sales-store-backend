package com.heamimont.salesstoreapi.dto.report;

import java.util.List;

// DTO representing one order in the report
public class OrderReportDTO {
    private Long orderId;
    private List<OrderProductReportDTO> products;
    private String userFullName;

    public OrderReportDTO() {}

    public OrderReportDTO(Long orderId, List<OrderProductReportDTO> products, String userFullName) {
        this.orderId = orderId;
        this.products = products;
        this.userFullName = userFullName;
    }
    // getters and setters
}
