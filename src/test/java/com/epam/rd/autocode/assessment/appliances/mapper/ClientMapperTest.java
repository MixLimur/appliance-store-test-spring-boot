package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private ClientMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClientMapper();
    }

    @Test
    @DisplayName("Should map Entity to DTO correctly")
    void toDto_ValidEntity_ReturnsDto() {
        Client entity = new Client();
        entity.setId(1L);
        entity.setName("Alice");
        entity.setEmail("alice@test.com");
        entity.setPassword("pass");
        entity.setCard("1234-5678");

        ClientDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@test.com", dto.getEmail());
        assertEquals("pass", dto.getPassword());
        assertEquals("1234-5678", dto.getCard());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toDto_NullEntity_ReturnsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    @DisplayName("Should map DTO to Entity correctly")
    void toEntity_ValidDto_ReturnsEntity() {
        ClientDto dto = new ClientDto(1L, "Alice", "alice@test.com", "pass", "1234-5678");

        Client entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Alice", entity.getName());
        assertEquals("alice@test.com", entity.getEmail());
        assertEquals("pass", entity.getPassword());
        assertEquals("1234-5678", entity.getCard());
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void toEntity_NullDto_ReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

}