package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.CreateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRowMapper {

    private final ApplianceMapper applianceMapper;

    public OrderRowResponse toResponse(OrderRow entity) {
        if (entity == null) {
            return null;
        }
        OrderRowResponse response = new OrderRowResponse();
        response.setId(entity.getId());
        response.setAppliance(applianceMapper.toResponse(entity.getAppliance()));
        response.setNumber(entity.getNumber());
        response.setAmount(entity.getAmount());
        return response;
    }

    public OrderRow toEntity(CreateOrderRowRequest request, Appliance appliance) {
        if (request == null) {
            return null;
        }
        OrderRow entity = new OrderRow();
        entity.setAppliance(appliance);
        entity.setNumber(request.getNumber());
        entity.setAmount(request.getAmount());
        return entity;
    }

    public void updateEntityFromRequest(UpdateOrderRowRequest request, OrderRow entity, Appliance appliance) {
        if (request == null || entity == null) {
            return;
        }
        entity.setAppliance(appliance);
        entity.setNumber(request.getNumber());
        entity.setAmount(request.getAmount());
    }
}
