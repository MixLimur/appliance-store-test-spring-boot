package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
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
    @DisplayName("Should map Entity to Response correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        Manufacturer entity = new Manufacturer();
        entity.setId(1L);
        entity.setName("Samsung");

        ManufacturerResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Samsung", response.getName());
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateManufacturerRequest to Entity correctly")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        CreateManufacturerRequest request = new CreateManufacturerRequest();
        request.setName("LG");

        Manufacturer entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("LG", entity.getName());
    }

    @Test
    @DisplayName("Should return null when CreateManufacturerRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should update existing Manufacturer entity from UpdateManufacturerRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        request.setId(1L);
        request.setName("Samsung Updated");

        Manufacturer entity = new Manufacturer();
        entity.setId(1L);
        entity.setName("Samsung");

        mapper.updateEntityFromRequest(request, entity);

        assertEquals("Samsung Updated", entity.getName());
    }
}