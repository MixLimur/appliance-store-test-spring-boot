package com.epam.rd.autocode.assessment.appliances.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public abstract class AbstractUpdateUserRequestTest <T extends UpdateUserRequest> {

    @Autowired
    protected Validator validator;

    protected abstract T createValidRequest();

    @Test
    @DisplayName("Should pass validation when user update fields are valid")
    void validUpdateUserFields_ShouldPass() {
        T request = createValidRequest();
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid update request should pass validation");
    }

    @Test
    @DisplayName("Should fail when ID is null")
    void nullId_ShouldFail() {
        T request = createValidRequest();
        request.setId(null);

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void blankName_ShouldFail() {
        T request = createValidRequest();
        request.setName("   ");

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
    }

    @Test
    @DisplayName("Should fail when name length is out of bounds")
    void outOfBoundsName_ShouldFail() {
        T shortRequest = createValidRequest();
        shortRequest.setName("A");
        assertEquals(1, validator.validate(shortRequest).size());

        T longRequest = createValidRequest();
        longRequest.setName("A".repeat(51));
        assertEquals(1, validator.validate(longRequest).size());
    }

    @ParameterizedTest(name = "Invalid email: ''{0}''")
    @ValueSource(strings = {"invalid-email", "test@", "@domain.com"})
    @DisplayName("Should fail when email is invalid")
    void invalidEmail_ShouldFail(String invalidEmail) {
        T request = createValidRequest();
        request.setEmail(invalidEmail);

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}
