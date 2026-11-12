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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("getAll() should return page of responses when keyword is null and sort direction is ASC")
    void getAll_WhenKeywordIsNull_ShouldReturnPageOfResponses() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<Employee> employeePage = new PageImpl<>(List.of(employeeEntity));
        when(repository.findAll(expectedPageable)).thenReturn(employeePage);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        Page<EmployeeResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeResponse, result.getContent().get(0));
        verify(repository).findAll(expectedPageable);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getAll() should ignore blank keyword and sort descending when sortDir is DESC")
    void getAll_WhenKeywordIsBlankAndSortDesc_ShouldReturnSortedPage() {
        Pageable expectedPageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "name"));
        Page<Employee> employeePage = new PageImpl<>(List.of(employeeEntity));
        when(repository.findAll(expectedPageable)).thenReturn(employeePage);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        Page<EmployeeResponse> result = service.getAll(1, 10, "name", "desc", "   ");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository).findAll(expectedPageable);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getAll() should trim keyword and fallback to ASC when sort direction is invalid")
    void getAll_WhenKeywordProvidedAndSortInvalid_ShouldSearchWithTrimmedKeyword() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "email"));
        Page<Employee> employeePage = new PageImpl<>(List.of(employeeEntity));
        when(repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                "Bob", "Bob", "Bob", expectedPageable))
                .thenReturn(employeePage);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        Page<EmployeeResponse> result = service.getAll(0, 5, "email", "invalid_dir", "  Bob  ");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                "Bob", "Bob", "Bob", expectedPageable);
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getAll() should return empty page when no employees exist")
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        when(repository.findAll(expectedPageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<EmployeeResponse> result = service.getAll(0, 5, "id", "asc", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll(expectedPageable);
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
    @DisplayName("getByEmail() should return employee response when employee exists")
    void getByEmail_WhenEmployeeExists_ShouldReturnResponse() {
        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(employeeEntity));
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.getByEmail("test@test.com");

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(repository).findByEmail("test@test.com");
        verify(mapper).toResponse(employeeEntity);
    }

    @Test
    @DisplayName("getByEmail() should throw ResourceNotFoundException when email not found")
    void getByEmail_WhenEmployeeNotFound_ShouldThrowException() {
        when(repository.findByEmail("absent@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByEmail("absent@test.com"));

        verify(repository).findByEmail("absent@test.com");
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should throw DuplicateEmailException if email exists")
    void create_WhenEmailExists_ShouldThrowException() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.of(employeeEntity));

        assertThrows(DuplicateEmailException.class, () -> service.create(createRequest));

        verify(repository, never()).save(any());
        verifyNoInteractions(mapper, passwordEncoder);
    }

    @Test
    @DisplayName("create() should save and return employee response when valid request")
    void create_WhenValidRequest_ShouldSaveAndReturnResponse() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.empty());
        when(mapper.toEntity(createRequest)).thenReturn(employeeEntity);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedSecurePass123");
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.toResponse(employeeEntity)).thenReturn(employeeResponse);

        EmployeeResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        assertEquals("encodedSecurePass123", employeeEntity.getPassword());
        verify(repository).findByEmail(createRequest.getEmail());
        verify(mapper).toEntity(createRequest);
        verify(passwordEncoder).encode("SecurePass123");
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
    @DisplayName("delete() should delete and flush employee when employee exists and is not referenced")
    void delete_WhenEmployeeExists_ShouldDeleteAndFlush() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
        verify(repository).flush();
    }

    @Test
    @DisplayName("delete() should throw IllegalStateException when employee is referenced in orders")
    void delete_WhenReferencedInOrders_ShouldThrowIllegalStateException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).flush();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.delete(1L));

        assertEquals("error.employee.delete.referenced", exception.getMessage());
        verify(repository).deleteById(1L);
        verify(repository).flush();
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when employee does not exist")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }
}