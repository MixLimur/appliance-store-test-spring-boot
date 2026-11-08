package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplianceServiceImplTest {
    @Mock
    private ApplianceRepository applianceRepository;
    @Mock
    private ManufacturerRepository manufacturerRepository;
    @Mock
    private ApplianceMapper applianceMapper;

    @InjectMocks
    private ApplianceServiceImpl service;

    private Appliance appliance;
    private ApplianceDto applianceDto;
    private Manufacturer manufacturer;
    private ManufacturerDto manufacturerDto;

    @BeforeEach
    void setUp() {
        manufacturer = new Manufacturer();
        manufacturer.setId(1L);

        manufacturerDto = new ManufacturerDto();
        manufacturerDto.setId(1L);

        appliance = new Appliance();
        appliance.setId(10L);
        appliance.setCategory(Category.BIG);
        appliance.setManufacturer(manufacturer);

        applianceDto = new ApplianceDto();
        applianceDto.setId(10L);
        applianceDto.setCategory(Category.BIG);
        applianceDto.setManufacturer(manufacturerDto);
    }

    @Test
    @DisplayName("create() should throw exception when Manufacturer does not exist")
    void create_WhenManufacturerNotFound_ShouldThrowException() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(applianceDto));

        verify(applianceRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() should proceed when Manufacturer exists")
    void create_WhenManufacturerExists_ShouldSaveAppliance() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(applianceMapper.toEntity(applianceDto)).thenReturn(appliance);
        when(applianceRepository.save(appliance)).thenReturn(appliance);
        when(applianceMapper.toDto(appliance)).thenReturn(applianceDto);

        ApplianceDto result = service.create(applianceDto);

        assertNotNull(result);
        verify(applianceRepository).save(appliance);
    }

    @Test
    @DisplayName("update() should throw exception when Appliance does not exist")
    void update_WhenApplianceNotFound_ShouldThrowException() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, applianceDto));
        verify(manufacturerRepository, never()).findById(anyLong());
        verify(applianceRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should update appliance when both appliance and manufacturer exist")
    void update_WhenBothExist_ShouldUpdateAndReturnDto() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(applianceRepository.save(appliance)).thenReturn(appliance);
        when(applianceMapper.toDto(appliance)).thenReturn(applianceDto);

        ApplianceDto result = service.update(10L, applianceDto);

        assertNotNull(result);
        verify(applianceRepository).save(appliance);
    }

    @Test
    @DisplayName("update() should throw exception when Appliance exists but Manufacturer does not")
    void update_WhenManufacturerNotFound_ShouldThrowException() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));

        when(manufacturerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, applianceDto));

        verify(applianceRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete appliance when found")
    void delete_WhenFound_ShouldDelete() {
        when(applianceRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(applianceRepository).deleteById(10L);
    }

    @Test
    @DisplayName("delete() should throw exception when Appliance not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(applianceRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(applianceRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getAll() should return list of ApplianceDto")
    void getAll_ShouldReturnList() {
        when(applianceRepository.findAll()).thenReturn(java.util.List.of(appliance));
        when(applianceMapper.toDto(appliance)).thenReturn(applianceDto);

        java.util.List<ApplianceDto> result = service.getAll();

        assertEquals(1, result.size());
        verify(applianceRepository).findAll();
    }

    @Test
    @DisplayName("getById() should return DTO when found")
    void getById_WhenFound_ShouldReturnDto() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));
        when(applianceMapper.toDto(appliance)).thenReturn(applianceDto);

        ApplianceDto result = service.getById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(applianceRepository).findById(10L);
    }

}