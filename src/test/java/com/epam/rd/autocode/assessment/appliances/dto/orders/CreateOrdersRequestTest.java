package com.epam.rd.autocode.assessment.appliances.dto.orders;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateOrdersRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all data is correct")
    void correctData_ShouldPassValidation() {
        CreateOrdersRequest request = createValidRequest();

        Set<ConstraintViolation<CreateOrdersRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Client information is missing",
            "uk, Інформація про клієнта відсутня"
    })
    @DisplayName("Should fail validation when clientId is null")
    void nullClient_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateOrdersRequest request = createValidRequest();
        request.setClientId(null);

        Set<ConstraintViolation<CreateOrdersRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private CreateOrdersRequest createValidRequest() {
        CreateOrdersRequest request = new CreateOrdersRequest();
        request.setClientId(5L);
        request.setEmployeeId(2L);
        request.setApproved(true);
        return request;
    }

}