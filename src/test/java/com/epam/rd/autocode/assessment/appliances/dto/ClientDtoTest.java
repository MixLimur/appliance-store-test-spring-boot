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
class ClientDtoTest {

    @Autowired
    private Validator validator;


    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        ClientDto dto = createValidClientDto();
        Set<ConstraintViolation<ClientDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid ClientDto");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Card number cannot be empty",
            "uk, Номер картки не може бути порожнім"
    })
    @DisplayName("Should return localized message when card is blank")
    void blankCard_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ClientDto dto = createValidClientDto();
        dto.setCard("   ");

        Set<ConstraintViolation<ClientDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private ClientDto createValidClientDto() {
        return new ClientDto(1L,
                "John Doe",
                "john.doe@example.com",
                "SecurePass123",
                "1234-1234-1234-1234");
    }

}