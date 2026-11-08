package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;

import java.util.List;

public interface OrderService {
    List<OrdersDto> getAll();
    OrdersDto getById(Long id);
    OrdersDto create(OrdersDto dto);
    OrdersDto update(Long id, OrdersDto dto);
    void delete(Long id);
}
