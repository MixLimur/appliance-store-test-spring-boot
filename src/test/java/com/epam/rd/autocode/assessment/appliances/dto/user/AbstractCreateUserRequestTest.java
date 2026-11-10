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
public abstract class AbstractCreateUserRequestTest <T extends CreateUserRequest> {
    @Autowired
    protected Validator validator;

    protected abstract T createValidRequest();

    @Test
    @DisplayName("Should pass validation when user fields are valid")
    void validUserFields_ShouldPass() {
        T request = createValidRequest();
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid request should pass validation");
    }

    @ParameterizedTest(name = "Invalid password: ''{0}''")
    @ValueSource(strings = {
            "Pass123",
            "password123",
            "PASSWORD123",
            "PasswordWord",
            "12345678"
    })
    @DisplayName("Should fail when password does not match complexity pattern")
    void invalidPassword_ShouldFail(String invalidPassword) {
        T request = createValidRequest();
        request.setPassword(invalidPassword);

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("{validation.user.password.complexity}", violations.iterator().next().getMessageTemplate());
    }

    @Test
    @DisplayName("Should fail when password is blank")
    void blankPassword_ShouldFail() {
        T request = createValidRequest();
        request.setPassword("   ");

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
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

    @Test
    @DisplayName("Should pass boundary values for name length")
    void boundaryName_ShouldPass() {
        T minRequest = createValidRequest();
        minRequest.setName("Jo");
        assertTrue(validator.validate(minRequest).isEmpty());

        T maxRequest = createValidRequest();
        maxRequest.setName("A".repeat(50));
        assertTrue(validator.validate(maxRequest).isEmpty());
    }

    @ParameterizedTest(name = "Invalid email: ''{0}''")
    @ValueSource(strings = {"invalid-email", "user@", "@domain.com", "user.domain.com"})
    @DisplayName("Should fail when email format is invalid")
    void invalidEmailFormat_ShouldFail(String invalidEmail) {
        T request = createValidRequest();
        request.setEmail(invalidEmail);

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void blankEmail_ShouldFail() {
        T request = createValidRequest();
        request.setEmail("   ");

        Set<ConstraintViolation<T>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
    }
}
