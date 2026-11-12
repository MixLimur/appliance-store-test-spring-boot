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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplianceRepository applianceRepository;
    private final OrdersMapper ordersMapper;

    @Override
    public Page<OrdersResponse> getAll(int page, int size, String sortField, String sortDir, String keyword, String userEmail, boolean isEmployee) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);
        Page<Orders> ordersPage = fetchOrdersPage(keyword, userEmail, isEmployee, pageable);

        List<OrdersResponse> content = ordersPage.stream()
                .map(ordersMapper::toResponse)
                .collect(Collectors.toList());

        if ("amount".equals(sortField)) {
            sortContentByAmount(content, sortDir);
        }

        return new PageImpl<>(content, pageable, ordersPage.getTotalElements());
    }

    @Override
    public OrdersResponse getById(Long id) {
        return ordersMapper.toResponse(findOrderById(id));
    }

    @Override
    @Transactional
    public OrdersResponse create(CreateOrdersRequest request) {
        Client client = findClientById(request.getClientId());
        Employee employee = findOptionalEmployeeById(request.getEmployeeId());

        Orders order = ordersMapper.toEntity(request, client, employee, Collections.emptySet());
        Orders savedOrder = ordersRepository.save(order);
        return ordersMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrdersResponse update(Long id, UpdateOrdersRequest request) {
        Orders existingOrder = findOrderById(id);
        ensureOrderIsNotApproved(existingOrder);

        Client client = findClientById(request.getClientId());
        Employee employee = findOptionalEmployeeById(request.getEmployeeId());
        Set<OrderRow> updatedOrderRows = mapOrderRowRequests(request);

        ordersMapper.updateEntityFromRequest(request, existingOrder, client, employee, updatedOrderRows);
        Orders updatedOrder = ordersRepository.save(existingOrder);
        return ordersMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Orders existingOrder = findOrderById(id);
        ensureOrderIsNotApproved(existingOrder);
        ordersRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignEmployee(Long orderId, String employeeEmail) {
        Orders existingOrder = findOrderById(orderId);
        ensureOrderIsNotApproved(existingOrder);

        Employee employee = findEmployeeByEmail(employeeEmail);
        existingOrder.setEmployee(employee);
        ordersRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void deleteOrderRow(Long orderId, Long rowId) {
        Orders existingOrder = findOrderById(orderId);
        ensureOrderIsNotApproved(existingOrder);

        existingOrder.getOrderRowSet().removeIf(row -> row.getId().equals(rowId));
        ordersRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void updateOrderRowNumber(Long orderId, Long rowId, Long number) {
        validateQuantity(number);

        Orders existingOrder = findOrderById(orderId);
        ensureOrderIsNotApproved(existingOrder);

        OrderRow targetRow = findOrderRowById(existingOrder, rowId);
        targetRow.setNumber(number);
        calculateAndSetRowAmount(targetRow, number);

        ordersRepository.save(existingOrder);
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        String actualSortField = "amount".equals(sortField) ? "id" : sortField;
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.ASC);
        return PageRequest.of(page, size, Sort.by(direction, actualSortField));
    }

    private Page<Orders> fetchOrdersPage(String keyword, String userEmail, boolean isEmployee, Pageable pageable) {
        return isEmployee ?
                ordersRepository.findAllWithFilter(keyword, pageable) :
                ordersRepository.findByClientEmailWithFilter(userEmail, keyword, pageable);
    }

    private void sortContentByAmount(List<OrdersResponse> content, String sortDir) {
        Comparator<OrdersResponse> comparator = Comparator.comparing(
                OrdersResponse::getAmount,
                Comparator.nullsFirst(BigDecimal::compareTo)
        );
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        content.sort(comparator);
    }

    private Orders findOrderById(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private Client findClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot process order. Client not found with id: " + clientId));
    }

    private Employee findOptionalEmployeeById(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot process order. Employee not found with id: " + employeeId));
    }

    private Employee findEmployeeByEmail(String employeeEmail) {
        return employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + employeeEmail));
    }

    private Appliance findApplianceById(Long applianceId) {
        return applianceRepository.findById(applianceId)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + applianceId));
    }

    private OrderRow findOrderRowById(Orders order, Long rowId) {
        return order.getOrderRowSet().stream()
                .filter(row -> row.getId().equals(rowId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order row not found with id: " + rowId));
    }

    private void ensureOrderIsNotApproved(Orders order) {
        if (Boolean.TRUE.equals(order.getApproved())) {
            throw new IllegalStateException("Cannot modify an approved order with id: " + order.getId());
        }
    }

    private void validateQuantity(Long number) {
        if (number == null || number < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }

    private Set<OrderRow> mapOrderRowRequests(UpdateOrdersRequest request) {
        if (request.getOrderRowSet() == null) {
            return Collections.emptySet();
        }
        return request.getOrderRowSet().stream()
                .map(rowRequest -> {
                    Appliance appliance = findApplianceById(rowRequest.getApplianceId());
                    OrderRow row = new OrderRow();
                    row.setId(rowRequest.getId());
                    row.setAppliance(appliance);
                    row.setNumber(rowRequest.getNumber());
                    row.setAmount(rowRequest.getAmount());
                    return row;
                })
                .collect(Collectors.toSet());
    }

    private void calculateAndSetRowAmount(OrderRow row, Long number) {
        if (row.getAppliance() != null && row.getAppliance().getPrice() != null) {
            row.setAmount(row.getAppliance().getPrice().multiply(BigDecimal.valueOf(number)));
        }
    }
}

