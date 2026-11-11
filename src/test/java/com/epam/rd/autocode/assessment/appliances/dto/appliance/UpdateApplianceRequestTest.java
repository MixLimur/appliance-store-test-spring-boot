package com.epam.rd.autocode.assessment.appliances.dto.appliance;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
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
class UpdateApplianceRequestTest {
    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void correctData_ShouldPassValidation() {
        UpdateApplianceRequest request = createValidUpdateRequest();
        Set<ConstraintViolation<UpdateApplianceRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when ID is null")
    void nullId_ShouldFail() {
        UpdateApplianceRequest request = createValidUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateApplianceRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void blankName_ShouldFail() {
        UpdateApplianceRequest request = createValidUpdateRequest();
        request.setName("   ");

        Set<ConstraintViolation<UpdateApplianceRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    private UpdateApplianceRequest createValidUpdateRequest() {
        UpdateApplianceRequest request = new UpdateApplianceRequest();
        request.setId(10L);
        request.setName("Refrigerator");
        request.setCategory(Category.BIG);
        request.setModel("RF-5000");
        request.setManufacturerId(1L);
        request.setPowerType(PowerType.AC220);
        request.setPower(250);
        request.setPrice(new BigDecimal("1200.50"));
        return request;
    }
}