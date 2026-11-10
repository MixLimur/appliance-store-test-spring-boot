package com.epam.rd.autocode.assessment.appliances.dto.manufacturer;

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
class CreateManufacturerRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should pass validation when data is correct")
    void correctData_ShouldPassValidation() {
        CreateManufacturerRequest request = new CreateManufacturerRequest();
        request.setName("Samsung");

        Set<ConstraintViolation<CreateManufacturerRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Manufacturer name cannot be empty",
            "uk, Назва виробника не може бути порожньою"
    })
    @DisplayName("Should return localized message when name is blank")
    void blankName_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateManufacturerRequest request = new CreateManufacturerRequest();
        request.setName("   ");

        Set<ConstraintViolation<CreateManufacturerRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

}