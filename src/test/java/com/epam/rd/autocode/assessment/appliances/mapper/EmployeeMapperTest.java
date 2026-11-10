package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class EmployeeMapperTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeMapper mapper;

    @Test
    @DisplayName("Should map Employee entity to EmployeeResponse correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        Employee entity = new Employee();
        entity.setId(2L);
        entity.setName("Bob");
        entity.setEmail("bob@test.com");
        entity.setPassword("encoded_pass");
        entity.setDepartment("Sales");

        EmployeeResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Bob", response.getName());
        assertEquals("bob@test.com", response.getEmail());
        assertEquals("Sales", response.getDepartment());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateEmployeeRequest to Employee entity with encoded password")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setName("Bob");
        request.setEmail("bob@test.com");
        request.setPassword("SecurePass123");
        request.setDepartment("Sales");

        when(passwordEncoder.encode("SecurePass123")).thenReturn("encoded_SecurePass123");

        Employee entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Bob", entity.getName());
        assertEquals("bob@test.com", entity.getEmail());
        assertEquals("encoded_SecurePass123", entity.getPassword());
        assertEquals("Sales", entity.getDepartment());
        verify(passwordEncoder).encode("SecurePass123");
    }

    @Test
    @DisplayName("Should return null when CreateEmployeeRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should update existing Employee entity fields from UpdateEmployeeRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setId(2L);
        request.setName("Bob Updated");
        request.setEmail("bob.updated@test.com");
        request.setDepartment("Marketing");

        Employee entity = new Employee();
        entity.setId(2L);
        entity.setName("Bob");
        entity.setEmail("bob@test.com");
        entity.setDepartment("Sales");

        mapper.updateEntityFromRequest(request, entity);

        assertEquals("Bob Updated", entity.getName());
        assertEquals("bob.updated@test.com", entity.getEmail());
        assertEquals("Marketing", entity.getDepartment());
    }
}