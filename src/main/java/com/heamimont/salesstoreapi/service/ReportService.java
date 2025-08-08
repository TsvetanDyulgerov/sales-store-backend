package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.dto.report.ReportMapper;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.OrderSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating reports based on orders.
 * Provides methods to filter orders by product name, username, and order date range.
 */

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves orders filtered by optional parameters:
     * productName, username, orderDate range.
     *
     * @param productName filter by product name (optional)
     * @param username filter by username (optional)
     * @param startDate filter orders from this date (inclusive) (optional)
     * @param endDate filter orders up to this date (inclusive) (optional)
     * @return list of OrderReportDTO matching filters
     */
    public List<OrderReportDTO> getFilteredOrders(
            String productName,
            String username,
            LocalDate startDate,
            LocalDate endDate) {

        Specification<Order> spec = null;

        if (productName != null && !productName.isBlank()) {
            Specification<Order> productSpec = OrderSpecifications.hasProductName(productName);
            spec = (spec == null) ? productSpec : spec.and(productSpec);
        }

        if (username != null && !username.isBlank()) {
            Specification<Order> usernameSpec = OrderSpecifications.hasUsername(username);
            spec = (spec == null) ? usernameSpec : spec.and(usernameSpec);
        }

        if (startDate != null) {
            Specification<Order> startDateSpec = OrderSpecifications.orderDateAfter(startDate);
            spec = (spec == null) ? startDateSpec : spec.and(startDateSpec);
        }

        if (endDate != null) {
            Specification<Order> endDateSpec = OrderSpecifications.orderDateBefore(endDate);
            spec = (spec == null) ? endDateSpec : spec.and(endDateSpec);
        }

        // If no filters, spec will be null -> fetch all orders
        List<Order> filteredOrders = null;
        try {
            filteredOrders = orderRepository.findAll(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch filtered orders: " + e.getMessage(), e);
        }

        return filteredOrders.stream()
                .map(ReportMapper::toOrderReportDTO)
                .collect(Collectors.toList());
    }
}
