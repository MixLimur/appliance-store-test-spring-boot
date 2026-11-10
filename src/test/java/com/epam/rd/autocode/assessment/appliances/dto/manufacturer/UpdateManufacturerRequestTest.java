package com.epam.rd.autocode.assessment.appliances.dto.manufacturer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpdateManufacturerRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void correctData_ShouldPassValidation() {
        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        request.setId(1L);
        request.setName("Samsung");

        Set<ConstraintViolation<UpdateManufacturerRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when ID is null")
    void nullId_ShouldFail() {
        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        request.setId(null);
        request.setName("Samsung");

        Set<ConstraintViolation<UpdateManufacturerRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void blankName_ShouldFail() {
        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        request.setId(1L);
        request.setName("   ");

        Set<ConstraintViolation<UpdateManufacturerRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }
}