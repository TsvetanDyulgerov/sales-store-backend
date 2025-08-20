package com.heamimont.salesstoreapi.dto.report;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderReportDTO {
    private UUID orderId;
    private List<OrderProductReportDTO> products;
    private String userFullName;

    public OrderReportDTO() {}

    public OrderReportDTO(UUID orderId, List<OrderProductReportDTO> products, String userFullName) {
        this.orderId = orderId; // Convert Long to UUID string
        this.products = products;
        this.userFullName = userFullName;
    }
}
