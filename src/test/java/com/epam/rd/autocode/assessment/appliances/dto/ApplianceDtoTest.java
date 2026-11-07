package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
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
class ApplianceDtoTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        ApplianceDto dto = createValidApplianceDto();

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid ApplianceDto");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Name cannot be empty",
            "uk, Назва не може бути порожньою"
    })
    @DisplayName("Should fail validation when name is blank")
    void blankName_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setName("   ");

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Category is required",
            "uk, Категорія є обов'язковою"
    })
    @DisplayName("Should fail validation when category is null")
    void nullCategory_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setCategory(null);

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Model cannot be empty",
            "uk, Модель не може бути порожньою"
    })
    @DisplayName("Should fail validation when model is blank")
    void blankModel_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setModel("");

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Manufacturer is required",
            "uk, Виробник є обов'язковим"
    })
    @DisplayName("Should fail validation when manufacturer is null")
    void nullManufacturer_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setManufacturer(null);

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Power type is required",
            "uk, Тип живлення є обов'язковим"
    })
    @DisplayName("Should fail validation when powerType is null")
    void nullPowerType_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setPowerType(null);

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Power must be greater than 0",
            "uk, Потужність повинна бути більшою за 0"
    })
    @DisplayName("Should fail validation when power is less than 1")
    void invalidPower_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setPower(0); // Ламаємо: 0 замість мінімум 1

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Price is required",
            "uk, Ціна є обов'язковою"
    })
    @DisplayName("Should fail validation when price is null")
    void nullPrice_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setPrice(null);

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Price must be greater than 0",
            "uk, Ціна повинна бути більшою за 0"
    })
    @DisplayName("Should fail validation when price is 0 or negative")
    void invalidPrice_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        ApplianceDto dto = createValidApplianceDto();
        dto.setPrice(new BigDecimal("0.00")); // Ламаємо: має бути мінімум 0.01

        Set<ConstraintViolation<ApplianceDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private ApplianceDto createValidApplianceDto() {
        ApplianceDto dto = new ApplianceDto();
        dto.setId(1L);
        dto.setName("Refrigerator");
        dto.setCategory(Category.BIG);
        dto.setModel("RF-5000");

        ManufacturerDto manufacturer = new ManufacturerDto();
        manufacturer.setId(1L);
        manufacturer.setName("Samsung");
        dto.setManufacturer(manufacturer);

        dto.setPowerType(PowerType.AC220);
        dto.setCharacteristic("No Frost");
        dto.setDescription("Large family refrigerator");
        dto.setPower(250);
        dto.setPrice(new BigDecimal("1200.50"));

        return dto;
    }


}