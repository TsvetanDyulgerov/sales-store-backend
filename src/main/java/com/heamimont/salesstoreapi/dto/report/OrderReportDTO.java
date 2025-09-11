package com.heamimont.salesstoreapi.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderReportDTO {
    @Schema(description = "Unique identifier for the order (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
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
