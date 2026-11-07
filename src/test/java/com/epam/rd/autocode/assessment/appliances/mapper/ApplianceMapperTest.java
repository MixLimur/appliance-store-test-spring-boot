package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
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
    private ApplianceDto dto;
    private Manufacturer manufacturer;
    private ManufacturerDto manufacturerDto;

    @BeforeEach
    void setUp() {
        manufacturer = new Manufacturer();
        manufacturer.setId(1L);

        manufacturerDto = new ManufacturerDto();
        manufacturerDto.setId(1L);

        entity = new Appliance();
        entity.setId(10L);
        entity.setName("Oven");
        entity.setCategory(Category.BIG);
        entity.setPrice(new BigDecimal("500.00"));
        entity.setPowerType(PowerType.AC220);
        entity.setManufacturer(manufacturer);
        entity.setPower(2000);
        entity.setModel("OV-100");

        dto = new ApplianceDto();
        dto.setId(10L);
        dto.setName("Oven");
        dto.setCategory(Category.BIG);
        dto.setPrice(new BigDecimal("500.00"));
        dto.setPowerType(PowerType.AC220);
        dto.setManufacturer(manufacturerDto);
        dto.setPower(2000);
        dto.setModel("OV-100");
    }

    @Test
    @DisplayName("Should map Entity to DTO")
    void toDto_ValidEntity_ReturnsDto() {
        when(manufacturerMapper.toDto(manufacturer)).thenReturn(manufacturerDto);

        ApplianceDto result = applianceMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Oven", result.getName());
        assertEquals(Category.BIG, result.getCategory());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertNotNull(result.getManufacturer());
        assertEquals(1L, result.getManufacturer().getId());

        verify(manufacturerMapper, times(1)).toDto(manufacturer);
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toDto_NullEntity_ReturnsNull() {
        assertNull(applianceMapper.toDto(null));
    }

    @Test
    @DisplayName("Should map DTO to Entity")
    void toEntity_ValidDto_ReturnsEntity() {
        when(manufacturerMapper.toEntity(manufacturerDto)).thenReturn(manufacturer);

        Appliance result = applianceMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Oven", result.getName());
        assertEquals(Category.BIG, result.getCategory());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertNotNull(result.getManufacturer());
        assertEquals(1L, result.getManufacturer().getId());

        verify(manufacturerMapper, times(1)).toEntity(manufacturerDto);
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void toEntity_NullDto_ReturnsNull() {
        assertNull(applianceMapper.toEntity(null));
    }

}