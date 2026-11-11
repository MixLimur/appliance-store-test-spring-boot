package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
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

    private Employee employeeEntity;
    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        employeeEntity = new Employee();
        employeeEntity.setId(1L);
        employeeEntity.setName("Bob");
        employeeEntity.setEmail("test@test.com");
        employeeEntity.setDepartment("Sales");

        createRequest = new CreateEmployeeRequest();
        createRequest.setName("Bob");
        createRequest.setEmail("test@test.com");
        createRequest.setPassword("SecurePass123");
        createRequest.setDepartment("Sales");

        updateRequest = new UpdateEmployeeRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Bob Updated");
        updateRequest.setEmail("test@test.com");
        updateRequest.setDepartment("Sales");

        employeeResponse = new EmployeeResponse(1L, "Bob", "test@test.com", "Sales");
    }

    @Test
    @DisplayName("getAll() should return list of employee responses when employees exist")
    void getAll_WhenEmployeesExist_ShouldReturnListOfResponses() {
        when(repository.findAll()).thenReturn(List.of(employeeEntity));
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        List<EmployeeResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employeeResponse, result.get(0));
        verify(repository).findAll();
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getAll() should return empty list when no employees exist")
    void getAll_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("getById() should return employee response when employee exists")
    void getById_WhenEmployeeExists_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.getById(1L);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(repository).findById(1L);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when employee does not exist")
    void getById_WhenEmployeeNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
        verify(repository).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should throw DuplicateEmailException if email exists")
    void create_WhenEmailExists_ShouldThrowException() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.of(employeeEntity));

        assertThrows(DuplicateEmailException.class, () -> service.create(createRequest));
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should save and return employee response when valid request")
    void create_WhenValidRequest_ShouldSaveAndReturnResponse() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.empty());
        when(mapper.toEntity(createRequest)).thenReturn(employeeEntity);
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(repository).findByEmail(createRequest.getEmail());
        verify(mapper).toEntity(createRequest);
        verify(repository).save(employeeEntity);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when employee to update is not found")
    void update_WhenEmployeeNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, updateRequest));
        verify(repository).findById(1L);
        verify(repository, never()).findByEmail(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw DuplicateEmailException if email is taken by another user")
    void update_WhenEmailTakenByAnother_ShouldThrowException() {
        Employee anotherEmployee = new Employee();
        anotherEmployee.setId(2L);
        anotherEmployee.setEmail("test@test.com");

        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));
        when(repository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(anotherEmployee));

        assertThrows(DuplicateEmailException.class, () -> service.update(1L, updateRequest));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should proceed if email belongs to the same user")
    void update_WhenEmailBelongsToSameUser_ShouldUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));
        when(repository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(employeeEntity));
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.update(1L, updateRequest);

        assertNotNull(result);
        verify(mapper).updateEntityFromRequest(updateRequest, employeeEntity);
        verify(repository).save(employeeEntity);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("update() should proceed when new email is not taken by anyone")
    void update_WhenEmailIsNewAndAvailable_ShouldUpdate() {
        updateRequest.setEmail("new.email@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));
        when(repository.findByEmail("new.email@example.com")).thenReturn(Optional.empty());
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.update(1L, updateRequest);

        assertNotNull(result);
        verify(mapper).updateEntityFromRequest(updateRequest, employeeEntity);
        verify(repository).save(employeeEntity);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("delete() should delete employee when employee exists")
    void delete_WhenEmployeeExists_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() should throw exception when employee not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }
}