package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersMapperTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private OrderRowMapper orderRowMapper;

    @InjectMocks
    private OrdersMapper ordersMapper;

    private Orders entity;
    private OrdersDto dto;
    private Client client;
    private ClientDto clientDto;
    private Employee employee;
    private EmployeeDto employeeDto;
    private OrderRow orderRow;
    private OrderRowDto orderRowDto;

    @BeforeEach
    void setUp() {
        client = new Client();
        clientDto = new ClientDto();

        employee = new Employee();
        employeeDto = new EmployeeDto();

        orderRow = new OrderRow();
        orderRowDto = new OrderRowDto();

        entity = new Orders();
        entity.setId(1L);
        entity.setApproved(true);
        entity.setClient(client);
        entity.setEmployee(employee);
        entity.setOrderRowSet(Set.of(orderRow));

        dto = new OrdersDto();
        dto.setId(1L);
        dto.setApproved(true);
        dto.setClient(clientDto);
        dto.setEmployee(employeeDto);
        dto.setOrderRowSet(Set.of(orderRowDto));
    }

    @Test
    @DisplayName("Should map Entity to DTO")
    void toDto_ValidEntity_ReturnsDto() {
        when(clientMapper.toDto(client)).thenReturn(clientDto);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        when(orderRowMapper.toDto(orderRow)).thenReturn(orderRowDto);

        OrdersDto result = ordersMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getApproved());
        assertNotNull(result.getClient());
        assertNotNull(result.getEmployee());
        assertEquals(1, result.getOrderRowSet().size());

        verify(clientMapper).toDto(client);
        verify(employeeMapper).toDto(employee);
        verify(orderRowMapper).toDto(orderRow);
    }

    @Test
    @DisplayName("Should map DTO to Entity")
    void toEntity_ValidDto_ReturnsEntity() {
        when(clientMapper.toEntity(clientDto)).thenReturn(client);
        when(employeeMapper.toEntity(employeeDto)).thenReturn(employee);
        when(orderRowMapper.toEntity(orderRowDto)).thenReturn(orderRow);

        Orders result = ordersMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getApproved());
        assertNotNull(result.getClient());
        assertNotNull(result.getEmployee());
        assertEquals(1, result.getOrderRowSet().size());

        verify(clientMapper).toEntity(clientDto);
        verify(employeeMapper).toEntity(employeeDto);
        verify(orderRowMapper).toEntity(orderRowDto);
    }

    @Test
    @DisplayName("Should handle null employee correctly")
    void toDto_NullEmployee_ReturnsDtoWithoutEmployee() {
        entity.setEmployee(null);
        when(clientMapper.toDto(client)).thenReturn(clientDto);
        when(orderRowMapper.toDto(orderRow)).thenReturn(orderRowDto);

        OrdersDto result = ordersMapper.toDto(entity);

        assertNull(result.getEmployee());
        verify(employeeMapper, never()).toDto(any());
    }

}