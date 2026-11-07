package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import org.springframework.stereotype.Component;

@Component
public class OrderRowMapper {
    private final ApplianceMapper applianceMapper;

    public OrderRowMapper(ApplianceMapper applianceMapper) {
        this.applianceMapper = applianceMapper;
    }

    public OrderRowDto toDto(OrderRow entity) {
        if (entity == null) {
            return null;
        }
        OrderRowDto dto = new OrderRowDto();
        dto.setId(entity.getId());
        dto.setAppliance(applianceMapper.toDto(entity.getAppliance()));
        dto.setNumber(entity.getNumber());
        dto.setAmount(entity.getAmount());
        return dto;
    }

    public OrderRow toEntity(OrderRowDto dto) {
        if (dto == null) {
            return null;
        }
        OrderRow entity = new OrderRow();
        entity.setId(dto.getId());
        entity.setAppliance(applianceMapper.toEntity(dto.getAppliance()));
        entity.setNumber(dto.getNumber());
        entity.setAmount(dto.getAmount());
        return entity;
    }
}
