package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplianceMapper {

    private final ManufacturerMapper manufacturerMapper;

    public ApplianceResponse toResponse(Appliance entity) {
        if (entity == null) {
            return null;
        }
        ApplianceResponse response = new ApplianceResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setModel(entity.getModel());
        response.setManufacturer(manufacturerMapper.toResponse(entity.getManufacturer()));
        response.setPowerType(entity.getPowerType());
        response.setCharacteristic(entity.getCharacteristic());
        response.setDescription(entity.getDescription());
        response.setPower(entity.getPower());
        response.setPrice(entity.getPrice());
        return response;
    }

    public Appliance toEntity(CreateApplianceRequest request, Manufacturer manufacturer) {
        if (request == null) {
            return null;
        }
        Appliance entity = new Appliance();
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setModel(request.getModel());
        entity.setManufacturer(manufacturer);
        entity.setPowerType(request.getPowerType());
        entity.setCharacteristic(request.getCharacteristic());
        entity.setDescription(request.getDescription());
        entity.setPower(request.getPower());
        entity.setPrice(request.getPrice());
        return entity;
    }

    public void updateEntityFromRequest(UpdateApplianceRequest request, Appliance entity, Manufacturer manufacturer) {
        if (request == null || entity == null) {
            return;
        }
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setModel(request.getModel());
        entity.setManufacturer(manufacturer);
        entity.setPowerType(request.getPowerType());
        entity.setCharacteristic(request.getCharacteristic());
        entity.setDescription(request.getDescription());
        entity.setPower(request.getPower());
        entity.setPrice(request.getPrice());
    }
}
