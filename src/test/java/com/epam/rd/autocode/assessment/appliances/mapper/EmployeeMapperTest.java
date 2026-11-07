package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeMapper();
    }

    @Test
    @DisplayName("Should map Entity to DTO correctly")
    void toDto_ValidEntity_ReturnsDto() {
        Employee entity = new Employee();
        entity.setId(2L);
        entity.setName("Bob");
        entity.setEmail("bob@test.com");
        entity.setPassword("pass");
        entity.setDepartment("Sales");

        EmployeeDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Bob", dto.getName());
        assertEquals("bob@test.com", dto.getEmail());
        assertEquals("pass", dto.getPassword());
        assertEquals("Sales", dto.getDepartment());
    }

    @Test
    @DisplayName("Should map DTO to Entity correctly")
    void toEntity_ValidDto_ReturnsEntity() {
        EmployeeDto dto = new EmployeeDto(2L, "Bob", "bob@test.com", "pass", "Sales");

        Employee entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Bob", entity.getName());
        assertEquals("bob@test.com", entity.getEmail());
        assertEquals("pass", entity.getPassword());
        assertEquals("Sales", entity.getDepartment());
    }

}