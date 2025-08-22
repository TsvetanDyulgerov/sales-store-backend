package com.heamimont.salesstoreapi.service;

import com.heamimont.salesstoreapi.dto.report.OrderReportDTO;
import com.heamimont.salesstoreapi.mapper.ReportMapper;
import com.heamimont.salesstoreapi.exceptions.ReportGenerationException;
import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ReportServiceTest {

    private OrderRepository orderRepository;
    private ReportMapper reportMapper;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        reportMapper = mock(ReportMapper.class);
        reportService = new ReportService(orderRepository, reportMapper);
    }

    @Test
    void testGetFilteredOrders_NoFilters_ReturnsAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        OrderReportDTO dto1 = new OrderReportDTO();
        OrderReportDTO dto2 = new OrderReportDTO();
        when(reportMapper.toOrderReportDTO(order1)).thenReturn(dto1);
        when(reportMapper.toOrderReportDTO(order2)).thenReturn(dto2);

        List<OrderReportDTO> result = reportService.getFilteredOrders(null, null, null, null);

        assertThat(result).containsExactly(dto1, dto2);

        verify(orderRepository).findAll();
        verify(reportMapper, times(2)).toOrderReportDTO(any());
    }


    @Test
    void testGetFilteredOrders_ProductNameFilter() {
        String productName = "laptop";

        Order order = new Order();
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));
        OrderReportDTO dto = new OrderReportDTO();
        when(reportMapper.toOrderReportDTO(order)).thenReturn(dto);

        reportService.getFilteredOrders(productName, null, null, null);

        ArgumentCaptor<Specification<Order>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(orderRepository).findAll(specCaptor.capture());

        Specification<Order> spec = specCaptor.getValue();
        assertThat(spec).isNotNull();

        verify(reportMapper).toOrderReportDTO(order);
    }

    @Test
    void testGetFilteredOrders_AllFilters() {
        String productName = "mouse";
        String username = "alice";
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 11, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 11, 30);

        Order order = new Order();
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));
        OrderReportDTO dto = new OrderReportDTO();
        when(reportMapper.toOrderReportDTO(order)).thenReturn(dto);

        List<OrderReportDTO> results = reportService.getFilteredOrders(productName, username, startDate, endDate);

        assertThat(results).containsExactly(dto);

        verify(orderRepository).findAll(any(Specification.class));
        verify(reportMapper).toOrderReportDTO(order);
    }

    @Test
    void testGetFilteredOrders_TrimInputs() {
        String productName = "  product  ";
        String username = "  user  ";

        Order order = new Order();
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));
        when(reportMapper.toOrderReportDTO(order)).thenReturn(new OrderReportDTO());

        reportService.getFilteredOrders(productName, username, null, null);

        ArgumentCaptor<Specification<Order>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(orderRepository).findAll(specCaptor.capture());

        assertThat(specCaptor.getValue()).isNotNull();
    }

    @Test
    void testGetFilteredOrders_ExceptionThrown_ThrowsReportGenerationException() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() ->
                reportService.getFilteredOrders(null, null, null, null)
        ).isInstanceOf(ReportGenerationException.class)
                .hasMessageContaining("Failed to fetch filtered orders");
    }

}
