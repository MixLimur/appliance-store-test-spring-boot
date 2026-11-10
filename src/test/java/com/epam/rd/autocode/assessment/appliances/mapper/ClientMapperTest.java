package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ClientMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientMapper mapper;

    @Test
    @DisplayName("Should map Client entity to ClientResponse correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        Client entity = new Client();
        entity.setId(1L);
        entity.setName("Alice");
        entity.setEmail("alice@test.com");
        entity.setPassword("encoded_pass");
        entity.setCard("1234-5678");

        ClientResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alice", response.getName());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("1234-5678", response.getCard());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateClientRequest to Client entity with encoded password")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("Alice");
        request.setEmail("alice@test.com");
        request.setPassword("SecurePass123");
        request.setCard("1234-5678");

        when(passwordEncoder.encode("SecurePass123")).thenReturn("encoded_SecurePass123");

        Client entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Alice", entity.getName());
        assertEquals("alice@test.com", entity.getEmail());
        assertEquals("encoded_SecurePass123", entity.getPassword());
        assertEquals("1234-5678", entity.getCard());
        verify(passwordEncoder).encode("SecurePass123");
    }

    @Test
    @DisplayName("Should return null when CreateClientRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should update existing Client entity fields from UpdateClientRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        UpdateClientRequest request = new UpdateClientRequest();
        request.setId(1L);
        request.setName("Alice Updated");
        request.setEmail("alice.updated@test.com");
        request.setCard("8765-4321");

        Client entity = new Client();
        entity.setId(1L);
        entity.setName("Alice");
        entity.setEmail("alice@test.com");
        entity.setCard("1234-5678");

        mapper.updateEntityFromRequest(request, entity);

        assertEquals("Alice Updated", entity.getName());
        assertEquals("alice.updated@test.com", entity.getEmail());
        assertEquals("8765-4321", entity.getCard());
    }

}