package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Page<OrdersResponse> getAll(int page, int size, String sortField, String sortDir, String keyword, String userEmail, boolean isEmployee);
    OrdersResponse getById(Long id);
    OrdersResponse create(CreateOrdersRequest request);
    OrdersResponse update(Long id, UpdateOrdersRequest request);
    void delete(Long id);
    void assignEmployee(Long orderId, String employeeEmail);
    void deleteOrderRow(Long orderId, Long rowId);
    void updateOrderRowNumber(Long orderId, Long rowId, Long number);
}
