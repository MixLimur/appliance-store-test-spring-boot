package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerRepository repository;

    @Mock
    private ManufacturerMapper mapper;

    @InjectMocks
    private ManufacturerServiceImpl service;

    private Manufacturer entity;
    private CreateManufacturerRequest createRequest;
    private UpdateManufacturerRequest updateRequest;
    private ManufacturerResponse response;

    @BeforeEach
    void setUp() {
        entity = new Manufacturer();
        entity.setId(1L);
        entity.setName("Samsung");

        createRequest = new CreateManufacturerRequest();
        createRequest.setName("Samsung");

        updateRequest = new UpdateManufacturerRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Samsung Updated");

        response = new ManufacturerResponse(1L, "Samsung");
    }

    @Test
    @DisplayName("getAll() should return list of responses when manufacturers exist")
    void getAll_WhenExist_ShouldReturnListOfResponses() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<ManufacturerResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(repository).findAll();
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("getAll() should return empty list when no manufacturers exist")
    void getAll_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ManufacturerResponse> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("getById() should return response when found")
    void getById_WhenFound_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ManufacturerResponse result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).findById(1L);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
        verify(repository).findById(99L);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should map, save and return response")
    void create_ShouldSaveAndReturnResponse() {
        when(mapper.toEntity(createRequest)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ManufacturerResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(response, result);
        verify(mapper).toEntity(createRequest);
        verify(repository).save(entity);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("update() should update entity and return response when found")
    void update_WhenFound_ShouldUpdateAndReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ManufacturerResponse result = service.update(1L, updateRequest);

        assertNotNull(result);
        verify(mapper).updateEntityFromRequest(updateRequest, entity);
        verify(repository).save(entity);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when manufacturer not found")
    void update_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, updateRequest));
        verify(repository).findById(99L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete entity when found")
    void delete_WhenFound_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }

}