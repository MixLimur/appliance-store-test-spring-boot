package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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
    private CreateApplianceRequest createRequest;
    private UpdateApplianceRequest updateRequest;
    private ApplianceResponse applianceResponse;
    private Manufacturer manufacturer;

    @BeforeEach
    void setUp() {
        manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("Samsung");

        appliance = new Appliance();
        appliance.setId(10L);
        appliance.setName("Oven");
        appliance.setCategory(Category.BIG);
        appliance.setManufacturer(manufacturer);

        createRequest = new CreateApplianceRequest();
        createRequest.setName("Oven");
        createRequest.setCategory(Category.BIG);
        createRequest.setManufacturerId(1L);

        updateRequest = new UpdateApplianceRequest();
        updateRequest.setId(10L);
        updateRequest.setName("Oven Updated");
        updateRequest.setCategory(Category.BIG);
        updateRequest.setManufacturerId(1L);

        ManufacturerResponse manufacturerResponse = new ManufacturerResponse(1L, "Samsung");
        applianceResponse = new ApplianceResponse(
                10L, "Oven", Category.BIG, "OV-100",
                manufacturerResponse, null, null, null, 2000, new BigDecimal("500.00")
        );
    }

    @Test
    @DisplayName("getAll() should return page of responses when keyword is null and sort direction is ASC")
    void getAll_WhenKeywordIsNull_ShouldReturnPageOfResponses() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<Appliance> appliancePage = new PageImpl<>(List.of(appliance));
        when(applianceRepository.findAll(expectedPageable)).thenReturn(appliancePage);
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        Page<ApplianceResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(applianceResponse, result.getContent().get(0));
        verify(applianceRepository).findAll(expectedPageable);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("getAll() should ignore blank keyword and sort descending when sortDir is DESC")
    void getAll_WhenKeywordIsBlankAndSortDesc_ShouldReturnSortedPage() {
        Pageable expectedPageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "price"));
        Page<Appliance> appliancePage = new PageImpl<>(List.of(appliance));
        when(applianceRepository.findAll(expectedPageable)).thenReturn(appliancePage);
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        Page<ApplianceResponse> result = service.getAll(1, 10, "price", "desc", "   ");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(applianceRepository).findAll(expectedPageable);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("getAll() should trim keyword and fallback to ASC when sort direction is invalid")
    void getAll_WhenKeywordProvidedAndSortInvalid_ShouldSearchWithTrimmedKeyword() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name"));
        Page<Appliance> appliancePage = new PageImpl<>(List.of(appliance));
        when(applianceRepository.findByNameContainingIgnoreCaseOrModelContainingIgnoreCaseOrManufacturerNameContainingIgnoreCase(
                "Oven", "Oven", "Oven", expectedPageable)).thenReturn(appliancePage);
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        Page<ApplianceResponse> result = service.getAll(0, 5, "name", "invalid_dir", "  Oven  ");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(applianceRepository).findByNameContainingIgnoreCaseOrModelContainingIgnoreCaseOrManufacturerNameContainingIgnoreCase(
                "Oven", "Oven", "Oven", expectedPageable);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("getAll() should return empty page when no appliances exist")
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        when(applianceRepository.findAll(expectedPageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ApplianceResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(applianceRepository).findAll(expectedPageable);
        verifyNoInteractions(applianceMapper);
    }

    @Test
    @DisplayName("getById() should return response when appliance is found")
    void getById_WhenFound_ShouldReturnResponse() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        ApplianceResponse result = service.getById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(applianceRepository).findById(10L);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when appliance is not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(applianceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));

        verify(applianceRepository).findById(99L);
        verifyNoInteractions(applianceMapper);
    }

    @Test
    @DisplayName("create() should save and return response when manufacturer exists")
    void create_WhenManufacturerExists_ShouldSaveAppliance() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(applianceMapper.toEntity(createRequest, manufacturer)).thenReturn(appliance);
        when(applianceRepository.save(appliance)).thenReturn(appliance);
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        ApplianceResponse result = service.create(createRequest);

        assertNotNull(result);
        verify(manufacturerRepository).findById(1L);
        verify(applianceMapper).toEntity(createRequest, manufacturer);
        verify(applianceRepository).save(appliance);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("create() should throw ResourceNotFoundException when manufacturer does not exist")
    void create_WhenManufacturerNotFound_ShouldThrowException() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createRequest));

        verify(applianceRepository, never()).save(any());
        verifyNoInteractions(applianceMapper);
    }

    @Test
    @DisplayName("update() should update and return response when both appliance and manufacturer exist")
    void update_WhenBothExist_ShouldUpdateAndReturnResponse() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(applianceRepository.save(appliance)).thenReturn(appliance);
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        ApplianceResponse result = service.update(10L, updateRequest);

        assertNotNull(result);
        verify(applianceMapper).updateEntityFromRequest(updateRequest, appliance, manufacturer);
        verify(applianceRepository).save(appliance);
        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when appliance does not exist")
    void update_WhenApplianceNotFound_ShouldThrowException() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));

        verify(manufacturerRepository, never()).findById(anyLong());
        verify(applianceRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when manufacturer does not exist")
    void update_WhenManufacturerNotFound_ShouldThrowException() {
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(appliance));
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));

        verify(applianceRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete and flush when appliance exists and is not referenced")
    void delete_WhenFound_ShouldDeleteAndFlush() {
        when(applianceRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(10L));

        verify(applianceRepository).existsById(10L);
        verify(applianceRepository).deleteById(10L);
        verify(applianceRepository).flush();
    }

    @Test
    @DisplayName("delete() should throw IllegalStateException when appliance is referenced in orders")
    void delete_WhenReferencedInOrders_ShouldThrowIllegalStateException() {
        when(applianceRepository.existsById(10L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(applianceRepository).flush();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.delete(10L));

        assertEquals("error.appliance.delete.referenced", exception.getMessage());
        verify(applianceRepository).deleteById(10L);
        verify(applianceRepository).flush();
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when appliance is not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(applianceRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(applianceRepository).existsById(99L);
        verify(applianceRepository, never()).deleteById(anyLong());
    }
}