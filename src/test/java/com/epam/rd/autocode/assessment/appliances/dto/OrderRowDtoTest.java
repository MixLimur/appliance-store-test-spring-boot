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

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRowDtoTest {

    @Autowired
    private Validator validator;

    private OrderRowDto createValidOrderRowDto() {
        OrderRowDto dto = new OrderRowDto();
        dto.setId(1L);

        ApplianceDto appliance = new ApplianceDto();
        appliance.setId(10L);
        dto.setAppliance(appliance);

        dto.setNumber(2L);
        dto.setAmount(new BigDecimal("2400.00"));

        return dto;
    }

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        OrderRowDto dto = createValidOrderRowDto();

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid OrderRowDto");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Appliance is required",
            "uk, Прилад є обов'язковим"
    })
    @DisplayName("Should fail validation when appliance is null")
    void nullAppliance_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrderRowDto dto = createValidOrderRowDto();
        dto.setAppliance(null);

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Number of items is required",
            "uk, Кількість одиниць є обов'язковою"
    })
    @DisplayName("Should fail validation when number is null")
    void nullNumber_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrderRowDto dto = createValidOrderRowDto();
        dto.setNumber(null);

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Number of items must be at least 1",
            "uk, Кількість одиниць має бути щонайменше 1"
    })
    @DisplayName("Should fail validation when number is less than 1")
    void invalidNumber_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrderRowDto dto = createValidOrderRowDto();
        dto.setNumber(0L);

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Amount is required",
            "uk, Сума є обов'язковою"
    })
    @DisplayName("Should fail validation when amount is null")
    void nullAmount_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrderRowDto dto = createValidOrderRowDto();
        dto.setAmount(null);

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Amount must be greater than 0",
            "uk, Сума повинна бути більшою за 0"
    })
    @DisplayName("Should fail validation when amount is 0 or negative")
    void invalidAmount_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        OrderRowDto dto = createValidOrderRowDto();
        dto.setAmount(new BigDecimal("0.00"));

        Set<ConstraintViolation<OrderRowDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

}