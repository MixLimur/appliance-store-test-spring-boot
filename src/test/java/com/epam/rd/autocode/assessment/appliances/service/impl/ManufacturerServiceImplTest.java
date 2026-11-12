package com.epam.rd.autocode.assessment.appliances.service.impl;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

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
    @DisplayName("getAll() should return page of responses when keyword is null and sort direction is ASC")
    void getAll_WhenKeywordIsNull_ShouldReturnPageOfResponses() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<Manufacturer> page = new PageImpl<>(List.of(entity));
        when(repository.findAll(expectedPageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<ManufacturerResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
        verify(repository).findAll(expectedPageable);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("getAll() should ignore blank keyword and sort descending when sortDir is DESC")
    void getAll_WhenKeywordIsBlankAndSortDesc_ShouldReturnSortedPage() {
        Pageable expectedPageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "name"));
        Page<Manufacturer> page = new PageImpl<>(List.of(entity));
        when(repository.findAll(expectedPageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<ManufacturerResponse> result = service.getAll(1, 10, "name", "desc", "   ");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository).findAll(expectedPageable);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("getAll() should trim keyword and fallback to ASC when sort direction is invalid")
    void getAll_WhenKeywordProvidedAndSortInvalid_ShouldSearchWithTrimmedKeyword() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name"));
        Page<Manufacturer> page = new PageImpl<>(List.of(entity));
        when(repository.findByNameContainingIgnoreCase("Samsung", expectedPageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<ManufacturerResponse> result = service.getAll(0, 5, "name", "invalid_dir", "  Samsung  ");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByNameContainingIgnoreCase("Samsung", expectedPageable);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("getAll() should return empty page when no manufacturers exist")
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        when(repository.findAll(expectedPageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ManufacturerResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll(expectedPageable);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("getById() should return response when manufacturer exists")
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
    @DisplayName("getById() should throw ResourceNotFoundException when manufacturer is not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));

        verify(repository).findById(99L);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should map, save and return manufacturer response")
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
    @DisplayName("update() should update entity and return response when manufacturer exists")
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
    @DisplayName("update() should throw ResourceNotFoundException when manufacturer is not found")
    void update_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, updateRequest));

        verify(repository).findById(99L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete and flush manufacturer when found and not referenced")
    void delete_WhenFound_ShouldDeleteAndFlush() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
        verify(repository).flush();
    }

    @Test
    @DisplayName("delete() should throw IllegalStateException when manufacturer is referenced in appliances")
    void delete_WhenReferencedInAppliances_ShouldThrowIllegalStateException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).flush();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.delete(1L));

        assertEquals("error.manufacturer.delete.referenced", exception.getMessage());
        verify(repository).deleteById(1L);
        verify(repository).flush();
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when manufacturer is not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }

}