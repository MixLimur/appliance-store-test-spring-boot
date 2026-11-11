package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ApplianceMapper applianceMapper;

    @Override
    public List<ApplianceResponse> getAll() {
        return applianceRepository.findAll().stream()
                .map(applianceMapper::toResponse)
                .toList();
    }

    @Override
    public ApplianceResponse getById(Long id) {
        return applianceRepository.findById(id)
                .map(applianceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + id));
    }

    @Override
    public ApplianceResponse create(CreateApplianceRequest request) {

        Long manufacturerId = request.getManufacturerId();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create appliance. Manufacturer not found with id: " + manufacturerId));

        Appliance appliance = applianceMapper.toEntity(request, manufacturer);
        Appliance savedAppliance = applianceRepository.save(appliance);
        return applianceMapper.toResponse(savedAppliance);
    }

    @Override
    public ApplianceResponse update(Long id, UpdateApplianceRequest request) {
        Appliance appliance = applianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + id));

        Long manufacturerId = request.getManufacturerId();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update appliance. Manufacturer not found with id: " + manufacturerId));

        applianceMapper.updateEntityFromRequest(request, appliance, manufacturer);

        Appliance updatedAppliance = applianceRepository.save(appliance);
        return applianceMapper.toResponse(updatedAppliance);
    }

    @Override
    public void delete(Long id) {
        if (!applianceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appliance not found with id: " + id);
        }
        applianceRepository.deleteById(id);
    }
}
