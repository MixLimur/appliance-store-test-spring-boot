package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrdersMapper {

    private final ClientMapper clientMapper;
    private final EmployeeMapper employeeMapper;
    private final OrderRowMapper orderRowMapper;

    public OrdersResponse toResponse(Orders entity) {
        if (entity == null) {
            return null;
        }
        OrdersResponse response = new OrdersResponse();
        response.setId(entity.getId());
        response.setApproved(entity.getApproved());
        response.setClient(clientMapper.toResponse(entity.getClient()));

        if (entity.getEmployee() != null) {
            response.setEmployee(employeeMapper.toResponse(entity.getEmployee()));
        }

        if (entity.getOrderRowSet() != null) {
            response.setOrderRowSet(entity.getOrderRowSet().stream()
                    .map(orderRowMapper::toResponse)
                    .collect(Collectors.toSet()));
        }
        return response;
    }

    public Orders toEntity(CreateOrdersRequest request, Client client, Employee employee, Set<OrderRow> orderRows) {
        if (request == null) {
            return null;
        }
        Orders entity = new Orders();
        entity.setApproved(request.getApproved() != null ? request.getApproved() : false);
        entity.setClient(client);
        entity.setEmployee(employee);
        entity.setOrderRowSet(orderRows);
        return entity;
    }

    public void updateEntityFromRequest(UpdateOrdersRequest request, Orders entity, Client client, Employee employee, Set<OrderRow> orderRows) {
        if (request == null || entity == null) {
            return;
        }
        entity.setApproved(request.getApproved());
        entity.setClient(client);
        entity.setEmployee(employee);

        if (entity.getOrderRowSet() != null) {
            entity.getOrderRowSet().clear();
            if (orderRows != null) {
                entity.getOrderRowSet().addAll(orderRows);
            }
        } else {
            entity.setOrderRowSet(orderRows);
        }
    }
}
