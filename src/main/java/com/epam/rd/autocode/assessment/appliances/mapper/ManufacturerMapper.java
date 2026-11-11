package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

    public ManufacturerResponse toResponse(Manufacturer entity) {
        if (entity == null) {
            return null;
        }
        return new ManufacturerResponse(entity.getId(), entity.getName());
    }

    public Manufacturer toEntity(CreateManufacturerRequest request) {
        if (request == null) {
            return null;
        }
        Manufacturer entity = new Manufacturer();
        entity.setName(request.getName());
        return entity;
    }

    public void updateEntityFromRequest(UpdateManufacturerRequest request, Manufacturer entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setName(request.getName());
    }
}
