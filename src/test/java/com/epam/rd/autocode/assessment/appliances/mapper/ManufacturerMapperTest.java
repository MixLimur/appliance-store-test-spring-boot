package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerMapperTest {

    private ManufacturerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ManufacturerMapper();
    }

    @Test
    @DisplayName("Should map Entity to DTO correctly")
    void toDto_ValidEntity_ReturnsDto() {
        Manufacturer entity = new Manufacturer();
        entity.setId(1L);
        entity.setName("Samsung");

        ManufacturerDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Samsung", dto.getName());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toDto_NullEntity_ReturnsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    @DisplayName("Should map DTO to Entity correctly")
    void toEntity_ValidDto_ReturnsEntity() {
        ManufacturerDto dto = new ManufacturerDto();
        dto.setId(2L);
        dto.setName("LG");

        Manufacturer entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("LG", entity.getName());
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void toEntity_NullDto_ReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

}