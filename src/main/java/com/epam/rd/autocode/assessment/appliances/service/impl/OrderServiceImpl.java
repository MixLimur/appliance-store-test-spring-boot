package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrdersMapper;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final OrdersMapper ordersMapper;

    @Override
    public List<OrdersDto> getAll() {
        return ordersRepository.findAll().stream()
                .map(ordersMapper::toDto)
                .toList();
    }

    @Override
    public OrdersDto getById(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ordersMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrdersDto create(OrdersDto dto) {
        Long clientId = dto.getClient().getId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create order. Client not found with id: " + clientId));

        Employee employee = null;
        if (dto.getEmployee() != null && dto.getEmployee().getId() != null) {
            Long employeeId = dto.getEmployee().getId();
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot create order. Employee not found with id: " + employeeId));
        }

        Orders order = ordersMapper.toEntity(dto);
        order.setId(null);
        order.setClient(client);
        order.setEmployee(employee);

        Orders savedOrder = ordersRepository.save(order);
        return ordersMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrdersDto update(Long id, OrdersDto dto) {
        Orders existingOrder = ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Long clientId = dto.getClient().getId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update order. Client not found with id: " + clientId));

        Employee employee = null;
        if (dto.getEmployee() != null && dto.getEmployee().getId() != null) {
            Long employeeId = dto.getEmployee().getId();
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update order. Employee not found with id: " + employeeId));
        }

        existingOrder.setApproved(dto.getApproved());
        existingOrder.setClient(client);
        existingOrder.setEmployee(employee);

        Orders mappedFromDto = ordersMapper.toEntity(dto);
        existingOrder.setOrderRowSet(mappedFromDto.getOrderRowSet());

        Orders updatedOrder = ordersRepository.save(existingOrder);
        return ordersMapper.toDto(updatedOrder);
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

