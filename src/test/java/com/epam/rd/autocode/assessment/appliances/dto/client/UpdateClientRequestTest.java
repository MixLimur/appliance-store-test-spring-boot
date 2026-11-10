package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.dto.user.AbstractUpdateUserRequestTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UpdateClientRequestTest extends AbstractUpdateUserRequestTest<UpdateClientRequest> {

    @Override
    protected UpdateClientRequest createValidRequest() {
        UpdateClientRequest request = new UpdateClientRequest();
        request.setId(1L);
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setCard("1234-5678-9012-3456");
        return request;
    }

    @Test
    @DisplayName("Should fail when card is blank")
    void blankCard_ShouldFail() {
        UpdateClientRequest request = createValidRequest();
        request.setCard("   ");

        Set<ConstraintViolation<UpdateClientRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}