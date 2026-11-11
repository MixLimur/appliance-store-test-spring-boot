package com.epam.rd.autocode.assessment.appliances.service.impl;

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

        if (Boolean.TRUE.equals(existingOrder.getApproved())) {
            throw new IllegalStateException("Cannot modify an approved order with id: " + id);
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
        Orders existingOrder = ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (Boolean.TRUE.equals(existingOrder.getApproved())) {
            throw new IllegalStateException("Cannot delete an approved order with id: " + id);
        }
        ordersRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignEmployee(Long orderId, String employeeEmail) {
        Orders existingOrder = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (Boolean.TRUE.equals(existingOrder.getApproved())) {
            throw new IllegalStateException("Cannot modify an approved order with id: " + orderId);
        }

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + employeeEmail));

        existingOrder.setEmployee(employee);
        ordersRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void deleteOrderRow(Long orderId, Long rowId) {
        Orders existingOrder = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (Boolean.TRUE.equals(existingOrder.getApproved())) {
            throw new IllegalStateException("Cannot modify an approved order with id: " + orderId);
        }

        existingOrder.getOrderRowSet().removeIf(row -> row.getId().equals(rowId));
        ordersRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void updateOrderRowNumber(Long orderId, Long rowId, Long number) {
        if (number == null || number < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Orders existingOrder = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (Boolean.TRUE.equals(existingOrder.getApproved())) {
            throw new IllegalStateException("Cannot modify an approved order with id: " + orderId);
        }

        OrderRow targetRow = existingOrder.getOrderRowSet().stream()
                .filter(row -> row.getId().equals(rowId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order row not found with id: " + rowId));

        targetRow.setNumber(number);

        if (targetRow.getAppliance() != null && targetRow.getAppliance().getPrice() != null) {
            targetRow.setAmount(targetRow.getAppliance().getPrice().multiply(java.math.BigDecimal.valueOf(number)));
        }

        ordersRepository.save(existingOrder);
    }
}

