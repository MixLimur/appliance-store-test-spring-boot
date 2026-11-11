package com.epam.rd.autocode.assessment.appliances.dto.orderRow;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpdateOrderRowRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void correctData_ShouldPassValidation() {
        UpdateOrderRowRequest request = createValidUpdateRequest();
        Set<ConstraintViolation<UpdateOrderRowRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when ID is null")
    void nullId_ShouldFail() {
        UpdateOrderRowRequest request = createValidUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateOrderRowRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when applianceId is null")
    void nullApplianceId_ShouldFail() {
        UpdateOrderRowRequest request = createValidUpdateRequest();
        request.setApplianceId(null);

        Set<ConstraintViolation<UpdateOrderRowRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    private UpdateOrderRowRequest createValidUpdateRequest() {
        UpdateOrderRowRequest request = new UpdateOrderRowRequest();
        request.setId(1L);
        request.setApplianceId(10L);
        request.setNumber(2L);
        request.setAmount(new BigDecimal("2400.00"));
        return request;
    }

}