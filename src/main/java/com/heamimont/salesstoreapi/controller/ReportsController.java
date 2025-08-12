package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports/
     * Retrieves a report of orders filtered by product name, username, and date range.
     *
     * @param productName the name of the product to filter by (optional)
     * @param username    the username to filter by (optional)
     * @param startDate   the start date for filtering (optional, format: yyyy-MM-dd)
     * @param endDate     the end date for filtering (optional, format: yyyy-MM-dd)
     * @return a list of OrderReportDTO containing the filtered order reports
     */
    @GetMapping()
    public ResponseEntity<List<OrderReportDTO>> getOrdersReport(@RequestParam(required = false) String productName,
                                                                @RequestParam(required = false) String username,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate) {

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        List<OrderReportDTO> report = reportService.getFilteredOrders(productName, username, start, end);
        return ResponseEntity.ok(report);

    }
}
