package com.epam.rd.autocode.assessment.appliances.dto.employee;

import com.epam.rd.autocode.assessment.appliances.dto.user.AbstractUpdateUserRequestTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpdateEmployeeRequestTest extends AbstractUpdateUserRequestTest<UpdateEmployeeRequest> {

    @Override
    protected UpdateEmployeeRequest createValidRequest() {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setId(1L);
        request.setName("Jane Smith");
        request.setEmail("jane.smith@example.com");
        request.setDepartment("Sales");
        return request;
    }

    @Test
    @DisplayName("Should fail when department is blank")
    void blankDepartment_ShouldFail() {
        UpdateEmployeeRequest request = createValidRequest();
        request.setDepartment("   ");

        Set<ConstraintViolation<UpdateEmployeeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

}