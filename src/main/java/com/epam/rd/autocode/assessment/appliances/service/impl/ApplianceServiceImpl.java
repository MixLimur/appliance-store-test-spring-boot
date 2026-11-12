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
    public Page<ApplianceResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);
        Page<Appliance> appliancePage = fetchAppliancesPage(keyword, pageable);
        return appliancePage.map(applianceMapper::toResponse);
    }

    @Override
    public ApplianceResponse getById(Long id) {
        return applianceMapper.toResponse(findApplianceById(id));
    }

    @Override
    @Transactional
    public ApplianceResponse create(CreateApplianceRequest request) {
        Manufacturer manufacturer = findManufacturerById(request.getManufacturerId());
        Appliance appliance = applianceMapper.toEntity(request, manufacturer);
        Appliance savedAppliance = applianceRepository.save(appliance);
        return applianceMapper.toResponse(savedAppliance);
    }

    @Override
    @Transactional
    public ApplianceResponse update(Long id, UpdateApplianceRequest request) {
        Appliance appliance = findApplianceById(id);
        Manufacturer manufacturer = findManufacturerById(request.getManufacturerId());

        applianceMapper.updateEntityFromRequest(request, appliance, manufacturer);
        Appliance updatedAppliance = applianceRepository.save(appliance);
        return applianceMapper.toResponse(updatedAppliance);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ensureApplianceExists(id);
        try {
            applianceRepository.deleteById(id);
            applianceRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.appliance.delete.referenced");
        }
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.ASC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Page<Appliance> fetchAppliancesPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            String sanitizedKeyword = keyword.trim();
            return applianceRepository.findByNameContainingIgnoreCaseOrModelContainingIgnoreCaseOrManufacturerNameContainingIgnoreCase(
                    sanitizedKeyword, sanitizedKeyword, sanitizedKeyword, pageable);
        }
        return applianceRepository.findAll(pageable);
    }

    private Appliance findApplianceById(Long id) {
        return applianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance not found with id: " + id));
    }

    private Manufacturer findManufacturerById(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + manufacturerId));
    }

    private void ensureApplianceExists(Long id) {
        if (!applianceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appliance not found with id: " + id);
        }
    }
}
