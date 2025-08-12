package com.heamimont.salesstoreapi.dto.report;

import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportMapper {

    public OrderReportDTO toOrderReportDTO(Order order) {
        if (order == null) {
            return null;
        }

        // Map list of ordered products
        List<OrderProductReportDTO> productsDTO = order.getOrderProducts()
                .stream()
                .map(this::toOrderProductReportDTO)
                .collect(Collectors.toList());

        // Build and return full DTO
        return new OrderReportDTO(
                order.getId(),
                productsDTO,
                order.getUser().getFirstName() + " " + order.getUser().getLastName()
        );
    }

    private OrderProductReportDTO toOrderProductReportDTO(OrderProduct orderProduct) {
        if (orderProduct == null) {
            return null;
        }

        return new OrderProductReportDTO(
                orderProduct.getProduct().getName(),
                orderProduct.getProductQuantity()
        );
    }
}
