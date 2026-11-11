package com.epam.rd.autocode.assessment.appliances.dto.orders;

import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpdateOrdersRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void correctData_ShouldPassValidation() {
        UpdateOrdersRequest request = createValidUpdateRequest();
        Set<ConstraintViolation<UpdateOrdersRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when ID is null")
    void nullId_ShouldFail() {
        UpdateOrdersRequest request = createValidUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateOrdersRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when clientId is null")
    void nullClientId_ShouldFail() {
        UpdateOrdersRequest request = createValidUpdateRequest();
        request.setClientId(null);

        Set<ConstraintViolation<UpdateOrdersRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    private UpdateOrdersRequest createValidUpdateRequest() {
        UpdateOrdersRequest request = new UpdateOrdersRequest();
        request.setId(1L);
        request.setClientId(5L);
        request.setEmployeeId(2L);
        request.setApproved(true);
        request.setOrderRowSet(Set.of(new UpdateOrderRowRequest()));
        return request;
    }

}