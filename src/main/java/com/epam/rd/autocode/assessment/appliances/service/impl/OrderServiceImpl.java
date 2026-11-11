package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrdersMapper;
import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplianceRepository applianceRepository;
    private final OrdersMapper ordersMapper;

    @Override
    public List<OrdersResponse> getAll() {
        return ordersRepository.findAll().stream()
                .map(ordersMapper::toResponse)
                .toList();
    }

    @Override
    public OrdersResponse getById(Long id) {
        return ordersRepository.findById(id)
                .map(ordersMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    }

    @Override
    @Transactional
    public OrdersResponse create(CreateOrdersRequest request) {
        Long clientId = request.getClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create order. Client not found with id: " + clientId));

        Employee employee = null;
        if (request.getEmployeeId() != null) {
            Long employeeId = request.getEmployeeId();
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot create order. Employee not found with id: " + employeeId));
        }

        Orders order = ordersMapper.toEntity(request, client, employee, Collections.emptySet());
        Orders savedOrder = ordersRepository.save(order);
        return ordersMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrdersResponse update(Long id, UpdateOrdersRequest request) {
        Orders existingOrder = ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Long clientId = request.getClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update order. Client not found with id: " + clientId));

        Employee employee = null;
        if (request.getEmployeeId() != null) {
            Long employeeId = request.getEmployeeId();
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update order. Employee not found with id: " + employeeId));
        }

        Set<OrderRow> updatedOrderRows = Collections.emptySet();
        if (request.getOrderRowSet() != null) {
            updatedOrderRows = request.getOrderRowSet().stream()
                    .map(rowRequest -> {
                        Appliance appliance = applianceRepository.findById(rowRequest.getApplianceId())
                                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + rowRequest.getApplianceId()));
                        OrderRow row = new OrderRow();
                        row.setId(rowRequest.getId());
                        row.setAppliance(appliance);
                        row.setNumber(rowRequest.getNumber());
                        row.setAmount(rowRequest.getAmount());
                        return row;
                    })
                    .collect(Collectors.toSet());
        }

        ordersMapper.updateEntityFromRequest(request, existingOrder, client, employee, updatedOrderRows);

        Orders updatedOrder = ordersRepository.save(existingOrder);
        return ordersMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!ordersRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        ordersRepository.deleteById(id);
    }
}

