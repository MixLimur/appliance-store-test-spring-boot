package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDtoTest {

    @Autowired
    private Validator validator;

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Name cannot be empty",
            "uk, Ім'я не може бути порожнім"
    })
    @DisplayName("Should return localized message when name is blank")
    void blankName_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        UserDto dto = createValidUserDto();
        dto.setName("   ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Email cannot be empty",
            "uk, Електронна пошта не може бути порожньою"
    })
    @DisplayName("Should return localized message when email is null")
    void nullEmail_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        UserDto dto = createValidUserDto();
        dto.setEmail(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Email should be valid",
            "uk, Неправильний формат електронної пошти"
    })
    @DisplayName("Should return localized message when email format is invalid")
    void invalidEmailFormat_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        UserDto dto = createValidUserDto();
        dto.setEmail("invalid-email-without-at-symbol");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Password cannot be empty",
            "uk, Пароль не може бути порожнім"
    })
    @DisplayName("Should return localized message when password is blank")
    void blankPassword_ShouldFailValidation_Localized(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        UserDto dto = createValidUserDto();
        dto.setPassword("");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        List<String> actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertTrue(actualMessages.contains(expectedMessage),
                "Expected message '" + expectedMessage + "' not found in violations");
    }

    @Test
    @DisplayName("Should detect multiple validation errors simultaneously")
    void multipleErrors_ShouldFailValidation() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        UserDto dto = createValidUserDto();

        dto.setName("   ");
        dto.setEmail("invalid-email-format");
        dto.setPassword("");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        List<String> actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertTrue(actualMessages.contains("Name cannot be empty"), "Missing error for name");
        assertTrue(actualMessages.contains("Email should be valid"), "Missing error for email format");
        assertTrue(actualMessages.contains("Password cannot be empty"), "Missing error for password");
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'', Password=''{1}'' -> Expected=''{2}''")
    @MethodSource("provideInvalidPasswordsAndLocales")
    @DisplayName("Should return localized complexity message when password does not meet requirements")
    void passwordComplexity_ShouldFailValidation_Localized(String languageTag, String invalidPassword, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        UserDto dto = createValidUserDto();
        dto.setPassword(invalidPassword);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size(), "Should find exactly 1 validation error for password complexity");
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should have zero validation errors when all data is correct")
    void correctData_ShouldPassValidation() {
        UserDto dto = createValidUserDto();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Should not find any validation errors for a valid DTO");
    }

    private UserDto createValidUserDto() {
        return new UserDto(1L,
                "John Doe",
                "john.doe@example.com",
                "SecurePass123");
    }

    static Stream<Arguments> provideInvalidPasswordsAndLocales() {
        String enMessage = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one number";
        String ukMessage = "Пароль має містити щонайменше 8 символів, хоча б одну велику літеру, одну малу літеру та одну цифру";

        return Stream.of(
                Arguments.of("en", "Short1!", enMessage),
                Arguments.of("en", "password123", enMessage),
                Arguments.of("en", "PASSWORD123", enMessage),
                Arguments.of("en", "PasswordWithoutNumbers", enMessage),

                Arguments.of("uk", "Short1!", ukMessage),
                Arguments.of("uk", "password123", ukMessage),
                Arguments.of("uk", "PASSWORD123", ukMessage),
                Arguments.of("uk", "PasswordWithoutNumbers", ukMessage)
        );
    }

}