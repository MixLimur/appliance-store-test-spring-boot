package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplianceMapperTest {

    @Mock
    private ManufacturerMapper manufacturerMapper;

    @InjectMocks
    private ApplianceMapper applianceMapper;

    private Appliance entity;
    private CreateApplianceRequest createRequest;
    private UpdateApplianceRequest updateRequest;
    private Manufacturer manufacturer;
    private ManufacturerResponse manufacturerResponse;

    @BeforeEach
    void setUp() {
        manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("Samsung");

        manufacturerResponse = new ManufacturerResponse(1L, "Samsung");

        entity = new Appliance();
        entity.setId(10L);
        entity.setName("Oven");
        entity.setCategory(Category.BIG);
        entity.setPrice(new BigDecimal("500.00"));
        entity.setPowerType(PowerType.AC220);
        entity.setManufacturer(manufacturer);
        entity.setPower(2000);
        entity.setModel("OV-100");

        createRequest = new CreateApplianceRequest();
        createRequest.setName("Oven");
        createRequest.setCategory(Category.BIG);
        createRequest.setPrice(new BigDecimal("500.00"));
        createRequest.setPowerType(PowerType.AC220);
        createRequest.setManufacturerId(1L);
        createRequest.setPower(2000);
        createRequest.setModel("OV-100");

        updateRequest = new UpdateApplianceRequest();
        updateRequest.setId(10L);
        updateRequest.setName("Oven Updated");
        updateRequest.setCategory(Category.BIG);
        updateRequest.setPrice(new BigDecimal("600.00"));
        updateRequest.setPowerType(PowerType.AC220);
        updateRequest.setManufacturerId(1L);
        updateRequest.setPower(2200);
        updateRequest.setModel("OV-200");
    }

    @Test
    @DisplayName("Should map Entity to Response correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        when(manufacturerMapper.toResponse(manufacturer)).thenReturn(manufacturerResponse);

        ApplianceResponse result = applianceMapper.toResponse(entity);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Oven", result.getName());
        assertEquals(Category.BIG, result.getCategory());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertNotNull(result.getManufacturer());
        assertEquals(1L, result.getManufacturer().getId());

        verify(manufacturerMapper).toResponse(manufacturer);
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(applianceMapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateApplianceRequest to Entity correctly")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        Appliance result = applianceMapper.toEntity(createRequest, manufacturer);

        assertNotNull(result);
        assertEquals("Oven", result.getName());
        assertEquals(Category.BIG, result.getCategory());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertNotNull(result.getManufacturer());
        assertEquals(1L, result.getManufacturer().getId());
    }

    @Test
    @DisplayName("Should return null when CreateApplianceRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(applianceMapper.toEntity(null, manufacturer));
    }

    @Test
    @DisplayName("Should update existing Appliance entity fields from UpdateApplianceRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        applianceMapper.updateEntityFromRequest(updateRequest, entity, manufacturer);

        assertEquals("Oven Updated", entity.getName());
        assertEquals(new BigDecimal("600.00"), entity.getPrice());
        assertEquals("OV-200", entity.getModel());
        assertEquals(2200, entity.getPower());
    }
}