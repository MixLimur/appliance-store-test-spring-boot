package com.epam.rd.autocode.assessment.appliances.dto.client;

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
class CreateClientRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void correctData_ShouldPassValidation() {
        CreateClientRequest request = createValidRequest();
        Set<ConstraintViolation<CreateClientRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid CreateClientRequest should have no validation errors");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Card number cannot be empty",
            "uk, Номер картки не може бути порожнім"
    })
    @DisplayName("Should return localized message when card number is blank")
    void blankCardNumber_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateClientRequest request = createValidRequest();
        request.setCard("   ");

        Set<ConstraintViolation<CreateClientRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void shortPassword_ShouldFailValidation() {
        CreateClientRequest request = createValidRequest();
        request.setPassword("123"); // Менше 6 символів

        Set<ConstraintViolation<CreateClientRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }

    private CreateClientRequest createValidRequest() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("SecurePass123");
        request.setCard("1234-1234-1234-1234");
        return request;
    }

}