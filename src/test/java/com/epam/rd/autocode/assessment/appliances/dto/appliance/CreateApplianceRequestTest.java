package com.epam.rd.autocode.assessment.appliances.dto.appliance;

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
class CreateApplianceRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        CreateApplianceRequest request = createValidRequest();

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Name cannot be empty",
            "uk, Назва не може бути порожньою"
    })
    @DisplayName("Should fail validation when name is blank")
    void blankName_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateApplianceRequest request = createValidRequest();
        request.setName("   ");

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setCategory(null);

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setModel("");

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Manufacturer is required",
            "uk, Виробник є обов'язковим"
    })
    @DisplayName("Should fail validation when manufacturerId is null")
    void nullManufacturer_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateApplianceRequest request = createValidRequest();
        request.setManufacturerId(null);

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setPowerType(null);

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setPower(0);

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setPrice(null);

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

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
        CreateApplianceRequest request = createValidRequest();
        request.setPrice(new BigDecimal("0.00"));

        Set<ConstraintViolation<CreateApplianceRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    private CreateApplianceRequest createValidRequest() {
        CreateApplianceRequest request = new CreateApplianceRequest();
        request.setName("Refrigerator");
        request.setCategory(Category.BIG);
        request.setModel("RF-5000");
        request.setManufacturerId(1L);
        request.setPowerType(PowerType.AC220);
        request.setCharacteristic("No Frost");
        request.setDescription("Large family refrigerator");
        request.setPower(250);
        request.setPrice(new BigDecimal("1200.50"));
        return request;
    }
}