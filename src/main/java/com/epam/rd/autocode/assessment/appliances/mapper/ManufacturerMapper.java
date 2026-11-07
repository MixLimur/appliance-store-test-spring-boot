package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

    public ManufacturerDto toDto(Manufacturer entity) {
        if (entity == null) {
            return null;
        }
        ManufacturerDto dto = new ManufacturerDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public Manufacturer toEntity(ManufacturerDto dto) {
        if (dto == null) {
            return null;
        }
        Manufacturer entity = new Manufacturer();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
