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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ApplianceMapper applianceMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ApplianceResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Appliance> appliancePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            appliancePage = applianceRepository.findByNameContainingIgnoreCaseOrModelContainingIgnoreCase(k, k, pageable);
        } else {
            appliancePage = applianceRepository.findAll(pageable);
        }

        return appliancePage.map(applianceMapper::toResponse);
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
        try {
            applianceRepository.deleteById(id);
            applianceRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.appliance.delete.referenced");
        }
    }
}
