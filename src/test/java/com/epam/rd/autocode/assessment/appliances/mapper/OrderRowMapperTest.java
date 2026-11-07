package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
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
class OrderRowMapperTest {

    @Mock
    private ApplianceMapper applianceMapper;

    @InjectMocks
    private OrderRowMapper orderRowMapper;

    private OrderRow entity;
    private OrderRowDto dto;
    private Appliance appliance;
    private ApplianceDto applianceDto;

    @BeforeEach
    void setUp() {
        appliance = new Appliance();
        appliance.setId(10L);

        applianceDto = new ApplianceDto();
        applianceDto.setId(10L);

        entity = new OrderRow();
        entity.setId(1L);
        entity.setNumber(2L);
        entity.setAmount(new BigDecimal("100.50"));
        entity.setAppliance(appliance);

        dto = new OrderRowDto();
        dto.setId(1L);
        dto.setNumber(2L);
        dto.setAmount(new BigDecimal("100.50"));
        dto.setAppliance(applianceDto);
    }

    @Test
    @DisplayName("Should map Entity to DTO")
    void toDto_ValidEntity_ReturnsDto() {
        when(applianceMapper.toDto(appliance)).thenReturn(applianceDto);

        OrderRowDto result = orderRowMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getNumber());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertNotNull(result.getAppliance());

        verify(applianceMapper, times(1)).toDto(appliance);
    }

    @Test
    @DisplayName("Should map DTO to Entity")
    void toEntity_ValidDto_ReturnsEntity() {
        when(applianceMapper.toEntity(applianceDto)).thenReturn(appliance);

        OrderRow result = orderRowMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getNumber());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertNotNull(result.getAppliance());

        verify(applianceMapper, times(1)).toEntity(applianceDto);
    }

}