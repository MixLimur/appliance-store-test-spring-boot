package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;

import java.util.List;

public interface ManufacturerService {
    List<ManufacturerResponse> getAll();
    ManufacturerResponse getById(Long id);
    ManufacturerResponse create(CreateManufacturerRequest request);
    ManufacturerResponse update(Long id, UpdateManufacturerRequest request);
    void delete(Long id);
}
