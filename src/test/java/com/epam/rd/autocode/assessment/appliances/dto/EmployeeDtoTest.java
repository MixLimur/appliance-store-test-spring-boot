package com.epam.rd.autocode.assessment.appliances.dto;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EmployeeDtoTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        EmployeeDto dto = createValidEmployeeDto();
        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid ClientDto");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Department cannot be empty",
            "uk, Відділ не може бути порожнім"
    })
    @DisplayName("Should return localized department when card is blank")
    void blankCard_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        EmployeeDto dto = createValidEmployeeDto();
        dto.setDepartment("   ");

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private EmployeeDto createValidEmployeeDto() {
        return new EmployeeDto(1L,
                "John Doe",
                "john.doe@example.com",
                "SecurePass123",
                "security");
    }
}