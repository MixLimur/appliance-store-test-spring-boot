package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override
    public List<ManufacturerDto> getAll() {
        return manufacturerRepository.findAll().stream()
                .map(manufacturerMapper::toDto)
                .toList();
    }

    @Override
    public ManufacturerDto getById(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));
        return manufacturerMapper.toDto(manufacturer);
    }

    @Override
    @Transactional
    public ManufacturerDto create(ManufacturerDto dto) {
        Manufacturer manufacturer = manufacturerMapper.toEntity(dto);
        manufacturer.setId(null);

        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
        return manufacturerMapper.toDto(savedManufacturer);
    }

    @Override
    @Transactional
    public ManufacturerDto update(Long id, ManufacturerDto dto) {
        Manufacturer existingManufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));

        existingManufacturer.setName(dto.getName());

        Manufacturer updatedManufacturer = manufacturerRepository.save(existingManufacturer);
        return manufacturerMapper.toDto(updatedManufacturer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer not found with id: " + id);
        }
        manufacturerRepository.deleteById(id);
    }
}
