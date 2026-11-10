package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.dto.user.AbstractCreateUserRequestTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CreateClientRequestTest extends AbstractCreateUserRequestTest<CreateClientRequest> {

    @Override
    protected CreateClientRequest createValidRequest() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("SecurePass123");
        request.setCard("1234-5678-9012-3456");
        return request;
    }

    @Test
    @DisplayName("Should fail when card is blank")
    void blankCard_ShouldFail() {
        CreateClientRequest request = createValidRequest();
        request.setCard("   ");

        Set<ConstraintViolation<CreateClientRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @ParameterizedTest(name = "[{index}] Locale=''{0}'' -> Expected=''{1}''")
    @CsvSource({
            "en, Card number cannot be empty",
            "uk, Номер картки не може бути порожнім"
    })
    @DisplayName("Should return localized message when card is blank")
    void blankCard_ShouldReturnLocalizedMessage(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag));
        CreateClientRequest request = createValidRequest();
        request.setCard("   ");

        Set<ConstraintViolation<CreateClientRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }
}