package com.heamimont.salesstoreapi.dto.report;

import lombok.Data;

import java.util.List;

@Data
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
}
