package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ManufacturerDtoTest {

    @Autowired
    private Validator validator;

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Manufacturer name cannot be empty",
            "uk, Назва виробника не може бути порожньою"
    })
    @DisplayName("Should return correct localized message when name is blank")
    void blankName_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ManufacturerDto dto = new ManufacturerDto();
        dto.setName("");

        Set<ConstraintViolation<ManufacturerDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");

        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}