package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override
    public Page<ManufacturerResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);
        Page<Manufacturer> manufacturerPage = fetchManufacturersPage(keyword, pageable);
        return manufacturerPage.map(manufacturerMapper::toResponse);
    }

    @Override
    public ManufacturerResponse getById(Long id) {
        return manufacturerMapper.toResponse(findManufacturerById(id));
    }

    @Override
    @Transactional
    public ManufacturerResponse create(CreateManufacturerRequest request) {
        Manufacturer manufacturer = manufacturerMapper.toEntity(request);
        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
        return manufacturerMapper.toResponse(savedManufacturer);
    }

    @Override
    @Transactional
    public ManufacturerResponse update(Long id, UpdateManufacturerRequest request) {
        Manufacturer existingManufacturer = findManufacturerById(id);
        manufacturerMapper.updateEntityFromRequest(request, existingManufacturer);
        Manufacturer updatedManufacturer = manufacturerRepository.save(existingManufacturer);
        return manufacturerMapper.toResponse(updatedManufacturer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ensureManufacturerExists(id);
        try {
            manufacturerRepository.deleteById(id);
            manufacturerRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.manufacturer.delete.referenced");
        }
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.ASC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Page<Manufacturer> fetchManufacturersPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            String sanitizedKeyword = keyword.trim();
            return manufacturerRepository.findByNameContainingIgnoreCase(sanitizedKeyword, pageable);
        }
        return manufacturerRepository.findAll(pageable);
    }

    private Manufacturer findManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));
    }

    private void ensureManufacturerExists(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer not found with id: " + id);
        }
    }
}
