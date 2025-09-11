package com.heamimont.salesstoreapi.controller;

import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "[ADMIN] Endpoints for generating order reports")
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
    @Operation(summary = "Get Orders Report", description = "Retrieve a report of orders filtered by product name, username, and date range. All parameters are optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of order reports"),
            @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<OrderReportDTO>> getOrdersReport(@RequestParam(required = false) String productName,
                                                                @RequestParam(required = false) String username,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate) {
        // Validate and parse the start and end dates
        LocalDateTime start;
        try {
            start = (startDate != null) ? LocalDateTime.parse(startDate) : null;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Validate and parse the end date
        LocalDateTime end;
        try {
            end = (endDate != null) ? LocalDateTime.parse(endDate) : null;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<OrderReportDTO> report = reportService.getFilteredOrders(productName, username, start, end);
        return ResponseEntity.ok(report);

    }
}
