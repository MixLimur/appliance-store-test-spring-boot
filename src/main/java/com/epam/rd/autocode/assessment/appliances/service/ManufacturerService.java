package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ManufacturerService {
    Page<ManufacturerResponse> getAll(int page, int size, String sortField, String sortDir, String keyword);
    ManufacturerResponse getById(Long id);
    ManufacturerResponse create(CreateManufacturerRequest request);
    ManufacturerResponse update(Long id, UpdateManufacturerRequest request);
    void delete(Long id);
}
