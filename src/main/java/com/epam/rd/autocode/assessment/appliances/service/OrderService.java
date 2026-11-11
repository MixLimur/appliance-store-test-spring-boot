package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;

import java.util.List;

public interface OrderService {
    List<OrdersResponse> getAll();
    OrdersResponse getById(Long id);
    OrdersResponse create(CreateOrdersRequest request);
    OrdersResponse update(Long id, UpdateOrdersRequest request);
    void delete(Long id);
    void assignEmployee(Long orderId, String employeeEmail);
    void deleteOrderRow(Long orderId, Long rowId);
    void updateOrderRowNumber(Long orderId, Long rowId, Long number);
}
