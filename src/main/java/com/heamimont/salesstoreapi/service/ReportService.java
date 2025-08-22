package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.mapper.ReportMapper;
import com.heamimont.salesstoreapi.exceptions.ReportGenerationException;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import com.heamimont.salesstoreapi.repository.OrderSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating reports based on orders.
 * Provides methods to filter orders by product name, username, and order date range.
 */
@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final ReportMapper reportMapper;

    public ReportService(OrderRepository orderRepository, ReportMapper reportMapper) {
        this.orderRepository = orderRepository;
        this.reportMapper = reportMapper;
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
    @Transactional(readOnly = true)
    public List<OrderReportDTO> getFilteredOrders(
            String productName,
            String username,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        Specification<Order> spec = null;

        if (productName != null && !productName.trim().isEmpty()) {
            Specification<Order> productSpec = OrderSpecifications.hasProductName(productName.trim());
            spec = productSpec;
        }

        if (username != null && !username.trim().isEmpty()) {
            Specification<Order> usernameSpec = OrderSpecifications.hasUsername(username.trim());
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

        List<Order> filteredOrders;
        try {
            if (spec == null) {
                filteredOrders = orderRepository.findAll(); // No filters
            } else {
                filteredOrders = orderRepository.findAll(spec);
            }
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to fetch filtered orders", e);
        }

        return filteredOrders.stream()
                .map(reportMapper::toOrderReportDTO)
                .collect(Collectors.toList());
    }
}
