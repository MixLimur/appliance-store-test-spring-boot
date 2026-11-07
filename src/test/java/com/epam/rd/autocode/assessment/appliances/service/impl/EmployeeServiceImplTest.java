package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl service;

    private Employee entity;
    private EmployeeDto dto;

    @BeforeEach
    void setUp() {
        entity = new Employee();
        entity.setId(1L);
        entity.setEmail("test@test.com");

        dto = new EmployeeDto();
        dto.setId(1L);
        dto.setEmail("test@test.com");
    }

    @Test
    @DisplayName("create() should throw DuplicateEmailException if email exists")
    void create_WhenEmailExists_ShouldThrowException() {
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));

        assertThrows(DuplicateEmailException.class, () -> service.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw DuplicateEmailException if email is taken by another user")
    void update_WhenEmailTakenByAnother_ShouldThrowException() {
        Employee anotherEmployee = new Employee();
        anotherEmployee.setId(2L);
        anotherEmployee.setEmail("test@test.com");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(anotherEmployee));

        assertThrows(DuplicateEmailException.class, () -> service.update(1L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should proceed if email belongs to the same user")
    void update_WhenEmailBelongsToSameUser_ShouldUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        EmployeeDto result = service.update(1L, dto);

        assertNotNull(result);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("delete() should throw exception when client not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}