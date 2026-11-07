package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrdersMapper {

    private final ClientMapper clientMapper;
    private final EmployeeMapper employeeMapper;
    private final OrderRowMapper orderRowMapper;

    public OrdersMapper(ClientMapper clientMapper, EmployeeMapper employeeMapper, OrderRowMapper orderRowMapper) {
        this.clientMapper = clientMapper;
        this.employeeMapper = employeeMapper;
        this.orderRowMapper = orderRowMapper;
    }

    public OrdersDto toDto(Orders entity) {
        if (entity == null) {
            return null;
        }
        OrdersDto dto = new OrdersDto();
        dto.setId(entity.getId());
        dto.setApproved(entity.getApproved());
        dto.setClient(clientMapper.toDto(entity.getClient()));

        if (entity.getEmployee() != null) {
            dto.setEmployee(employeeMapper.toDto(entity.getEmployee()));
        }

        if (entity.getOrderRowSet() != null) {
            dto.setOrderRowSet(entity.getOrderRowSet().stream()
                    .map(orderRowMapper::toDto)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public Orders toEntity(OrdersDto dto) {
        if (dto == null) {
            return null;
        }
        Orders entity = new Orders();
        entity.setId(dto.getId());
        entity.setApproved(dto.getApproved() != null ? dto.getApproved() : false);
        entity.setClient(clientMapper.toEntity(dto.getClient()));

        if (dto.getEmployee() != null) {
            entity.setEmployee(employeeMapper.toEntity(dto.getEmployee()));
        }

        if (dto.getOrderRowSet() != null) {
            entity.setOrderRowSet(dto.getOrderRowSet().stream()
                    .map(orderRowMapper::toEntity)
                    .collect(Collectors.toSet()));
        }
        return entity;
    }
}
