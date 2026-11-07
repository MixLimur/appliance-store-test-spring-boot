package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplianceMapper {

    private final ManufacturerMapper manufacturerMapper;

    public ApplianceDto toDto(Appliance entity) {
        if (entity == null) {
            return null;
        }
        ApplianceDto dto = new ApplianceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setModel(entity.getModel());
        dto.setManufacturer(manufacturerMapper.toDto(entity.getManufacturer()));
        dto.setPowerType(entity.getPowerType());
        dto.setCharacteristic(entity.getCharacteristic());
        dto.setDescription(entity.getDescription());
        dto.setPower(entity.getPower());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public Appliance toEntity(ApplianceDto dto) {
        if (dto == null) {
            return null;
        }
        Appliance entity = new Appliance();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setModel(dto.getModel());
        entity.setManufacturer(manufacturerMapper.toEntity(dto.getManufacturer()));
        entity.setPowerType(dto.getPowerType());
        entity.setCharacteristic(dto.getCharacteristic());
        entity.setDescription(dto.getDescription());
        entity.setPower(dto.getPower());
        entity.setPrice(dto.getPrice());
        return entity;
    }
}
