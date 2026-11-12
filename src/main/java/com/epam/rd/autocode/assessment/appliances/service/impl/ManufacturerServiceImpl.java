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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ManufacturerResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Manufacturer> manufacturerPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            manufacturerPage = manufacturerRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            manufacturerPage = manufacturerRepository.findAll(pageable);
        }

        return manufacturerPage.map(manufacturerMapper::toResponse);
    }

    @Override
    public ManufacturerResponse getById(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));
        return manufacturerMapper.toResponse(manufacturer);
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
        Manufacturer existingManufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));

        manufacturerMapper.updateEntityFromRequest(request, existingManufacturer);

        Manufacturer updatedManufacturer = manufacturerRepository.save(existingManufacturer);
        return manufacturerMapper.toResponse(updatedManufacturer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer not found with id: " + id);
        }
        try {
            manufacturerRepository.deleteById(id);
            manufacturerRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.manufacturer.delete.referenced");
        }
    }
}
