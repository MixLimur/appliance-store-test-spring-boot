package com.epam.rd.autocode.assessment.appliances.dto.employee;

import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.user.AbstractCreateUserRequestTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateEmployeeRequestTest extends AbstractCreateUserRequestTest<CreateEmployeeRequest> {

    @Override
    protected CreateEmployeeRequest createValidRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setName("Jane Smith");
        request.setEmail("jane.smith@example.com");
        request.setPassword("SecurePass123");
        request.setDepartment("Sales");
        return request;
    }

    @Test
    @DisplayName("Should fail when department is blank")
    void blankDepartment_ShouldFail() {
        CreateEmployeeRequest request = createValidRequest();
        request.setDepartment("   ");

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

}