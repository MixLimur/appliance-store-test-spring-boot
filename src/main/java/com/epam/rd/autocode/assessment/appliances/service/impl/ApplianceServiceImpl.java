package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
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
    public List<ApplianceDto> getAll() {
        return applianceRepository.findAll().stream()
                .map(applianceMapper::toDto)
                .toList();
    }

    @Override
    public ApplianceDto getById(Long id) {
        Appliance appliance = applianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + id));
        return applianceMapper.toDto(appliance);
    }

    @Override
    public ApplianceDto create(ApplianceDto dto) {

        Long manufacturerId = dto.getManufacturer().getId();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create appliance. Manufacturer not found with id: " + manufacturerId));

        Appliance appliance = applianceMapper.toEntity(dto);
        appliance.setId(null);

        Appliance savedAppliance = applianceRepository.save(appliance);
        return applianceMapper.toDto(savedAppliance);
    }

    @Override
    public ApplianceDto update(Long id, ApplianceDto dto) {
        Appliance existingAppliance = applianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + id));

        Long manufacturerId = dto.getManufacturer().getId();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update appliance. Manufacturer not found with id: " + manufacturerId));

        existingAppliance.setName(dto.getName());
        existingAppliance.setCategory(dto.getCategory());
        existingAppliance.setModel(dto.getModel());
        existingAppliance.setManufacturer(manufacturer);
        existingAppliance.setPowerType(dto.getPowerType());
        existingAppliance.setCharacteristic(dto.getCharacteristic());
        existingAppliance.setDescription(dto.getDescription());
        existingAppliance.setPower(dto.getPower());
        existingAppliance.setPrice(dto.getPrice());

        Appliance updatedAppliance = applianceRepository.save(existingAppliance);
        return applianceMapper.toDto(updatedAppliance);
    }

    @Override
    public void delete(Long id) {
        if (!applianceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appliance not found with id: " + id);
        }
        applianceRepository.deleteById(id);
    }
}
