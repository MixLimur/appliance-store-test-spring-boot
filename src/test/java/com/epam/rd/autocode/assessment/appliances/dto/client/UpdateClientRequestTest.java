package com.epam.rd.autocode.assessment.appliances.dto.client;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpdateClientRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields (including ID) are valid")
    void correctData_ShouldPassValidation() {
        UpdateClientRequest request = createValidUpdateRequest();
        Set<ConstraintViolation<UpdateClientRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when ID is null")
    void nullId_ShouldFailValidation() {
        UpdateClientRequest request = createValidUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateClientRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    private UpdateClientRequest createValidUpdateRequest() {
        UpdateClientRequest request = new UpdateClientRequest();
        request.setId(1L);
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setCard("1234-1234-1234-1234");
        return request;
    }
}