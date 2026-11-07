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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrdersDtoTest {

    @Autowired
    private Validator validator;

    // Фабричний метод
    private OrdersDto createValidOrdersDto() {
        OrdersDto dto = new OrdersDto();
        dto.setId(1L);
        dto.setApproved(true);

        ClientDto client = new ClientDto();
        client.setId(5L);
        dto.setClient(client);

        dto.setEmployee(null);

        return dto;
    }

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        OrdersDto dto = createValidOrdersDto();

        Set<ConstraintViolation<OrdersDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid OrdersDto");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Client information is missing",
            "uk, Інформація про клієнта відсутня"
    })
    @DisplayName("Should fail validation when client is null")
    void nullClient_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrdersDto dto = createValidOrdersDto();
        dto.setClient(null);

        Set<ConstraintViolation<OrdersDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

}