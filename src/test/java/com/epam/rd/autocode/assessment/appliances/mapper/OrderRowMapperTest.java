package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.CreateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
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
    private CreateOrderRowRequest createRequest;
    private UpdateOrderRowRequest updateRequest;
    private Appliance appliance;
    private ApplianceResponse applianceResponse;

    @BeforeEach
    void setUp() {
        appliance = new Appliance();
        appliance.setId(10L);

        applianceResponse = new ApplianceResponse();
        applianceResponse.setId(10L);

        entity = new OrderRow();
        entity.setId(1L);
        entity.setNumber(2L);
        entity.setAmount(new BigDecimal("100.50"));
        entity.setAppliance(appliance);

        createRequest = new CreateOrderRowRequest();
        createRequest.setApplianceId(10L);
        createRequest.setNumber(2L);
        createRequest.setAmount(new BigDecimal("100.50"));

        updateRequest = new UpdateOrderRowRequest();
        updateRequest.setId(1L);
        updateRequest.setApplianceId(10L);
        updateRequest.setNumber(3L);
        updateRequest.setAmount(new BigDecimal("150.75"));
    }

    @Test
    @DisplayName("Should map Entity to Response correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        when(applianceMapper.toResponse(appliance)).thenReturn(applianceResponse);

        OrderRowResponse result = orderRowMapper.toResponse(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getNumber());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertNotNull(result.getAppliance());
        assertEquals(10L, result.getAppliance().getId());

        verify(applianceMapper).toResponse(appliance);
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(orderRowMapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateOrderRowRequest to Entity correctly")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        OrderRow result = orderRowMapper.toEntity(createRequest, appliance);

        assertNotNull(result);
        assertEquals(2L, result.getNumber());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertNotNull(result.getAppliance());
        assertEquals(10L, result.getAppliance().getId());
    }

    @Test
    @DisplayName("Should return null when CreateOrderRowRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(orderRowMapper.toEntity(null, appliance));
    }

    @Test
    @DisplayName("Should update existing OrderRow entity fields from UpdateOrderRowRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        orderRowMapper.updateEntityFromRequest(updateRequest, entity, appliance);

        assertEquals(3L, entity.getNumber());
        assertEquals(new BigDecimal("150.75"), entity.getAmount());
        assertEquals(10L, entity.getAppliance().getId());
    }

}