package com.heamimont.salesstoreapi.dto.report;

import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMapper {

    public static OrderReportDTO toOrderReportDTO(Order order) {
        if (order == null) {
            return null;
        }


        // Map list of ordered products
        List<OrderProductReportDTO> productsDTO = order.getOrderProducts()
                .stream()
                .map(ReportMapper::toOrderProductReportDTO)
                .collect(Collectors.toList());

        // Build and return full DTO
        return new OrderReportDTO(
                order.getId(),
                productsDTO,
                order.getUser().getFirstName() + " " + order.getUser().getLastName()
        );
    }

    private static OrderProductReportDTO toOrderProductReportDTO(OrderProduct orderProduct) {
        if (orderProduct == null) {
            return null;
        }

        return new OrderProductReportDTO(
                orderProduct.getProduct().getName(),
                orderProduct.getProductQuantity()
        );
    }
}
