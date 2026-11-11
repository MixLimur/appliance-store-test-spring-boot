package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
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

import java.util.HashSet;
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
    private CreateOrdersRequest createRequest;
    private UpdateOrdersRequest updateRequest;
    private Client client;
    private ClientResponse clientResponse;
    private Employee employee;
    private EmployeeResponse employeeResponse;
    private OrderRow orderRow;
    private OrderRowResponse orderRowResponse;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(5L);
        clientResponse = new ClientResponse(5L, "John Doe", "john@example.com", "1234-5678");

        employee = new Employee();
        employee.setId(2L);
        employeeResponse = new EmployeeResponse(2L, "Jane Smith", "jane@example.com", "Sales");

        orderRow = new OrderRow();
        orderRow.setId(10L);
        orderRowResponse = new OrderRowResponse();
        orderRowResponse.setId(10L);

        entity = new Orders();
        entity.setId(1L);
        entity.setApproved(true);
        entity.setClient(client);
        entity.setEmployee(employee);
        entity.setOrderRowSet(new HashSet<>(Set.of(orderRow)));

        createRequest = new CreateOrdersRequest();
        createRequest.setClientId(5L);
        createRequest.setEmployeeId(2L);
        createRequest.setApproved(true);

        updateRequest = new UpdateOrdersRequest();
        updateRequest.setId(1L);
        updateRequest.setClientId(5L);
        updateRequest.setEmployeeId(2L);
        updateRequest.setApproved(false);
    }

    @Test
    @DisplayName("Should map Entity to Response correctly")
    void toResponse_ValidEntity_ReturnsResponse() {
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);
        when(orderRowMapper.toResponse(orderRow)).thenReturn(orderRowResponse);

        OrdersResponse result = ordersMapper.toResponse(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getApproved());
        assertNotNull(result.getClient());
        assertEquals(5L, result.getClient().getId());
        assertNotNull(result.getEmployee());
        assertEquals(2L, result.getEmployee().getId());
        assertEquals(1, result.getOrderRowSet().size());

        verify(clientMapper).toResponse(client);
        verify(employeeMapper).toResponse(employee);
        verify(orderRowMapper).toResponse(orderRow);
    }

    @Test
    @DisplayName("Should return null when Entity is null")
    void toResponse_NullEntity_ReturnsNull() {
        assertNull(ordersMapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map CreateOrdersRequest to Entity correctly")
    void toEntity_ValidCreateRequest_ReturnsEntity() {
        Orders result = ordersMapper.toEntity(createRequest, client, employee, Set.of(orderRow));

        assertNotNull(result);
        assertTrue(result.getApproved());
        assertEquals(client, result.getClient());
        assertEquals(employee, result.getEmployee());
        assertEquals(1, result.getOrderRowSet().size());
    }

    @Test
    @DisplayName("Should return null when CreateOrdersRequest is null")
    void toEntity_NullRequest_ReturnsNull() {
        assertNull(ordersMapper.toEntity(null, client, employee, Set.of(orderRow)));
    }

    @Test
    @DisplayName("Should handle null employee when mapping Entity to Response")
    void toResponse_NullEmployee_ReturnsResponseWithoutEmployee() {
        entity.setEmployee(null);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);
        when(orderRowMapper.toResponse(orderRow)).thenReturn(orderRowResponse);

        OrdersResponse result = ordersMapper.toResponse(entity);

        assertNotNull(result);
        assertNull(result.getEmployee());
        verify(employeeMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should update existing Orders entity from UpdateOrdersRequest")
    void updateEntityFromRequest_ValidUpdateRequest_UpdatesEntity() {
        ordersMapper.updateEntityFromRequest(updateRequest, entity, client, employee, Set.of(orderRow));

        assertFalse(entity.getApproved());
        assertEquals(client, entity.getClient());
        assertEquals(employee, entity.getEmployee());
    }

    @Test
    @DisplayName("Should set orderRowSet directly when entity orderRowSet is null")
    void updateEntityFromRequest_NullEntityOrderRowSet_SetsOrderRowSet() {
        entity.setOrderRowSet(null);
        Set<OrderRow> newOrderRows = Set.of(orderRow);

        ordersMapper.updateEntityFromRequest(updateRequest, entity, client, employee, newOrderRows);

        assertNotNull(entity.getOrderRowSet());
        assertEquals(newOrderRows, entity.getOrderRowSet());
        assertFalse(entity.getApproved());
        assertEquals(client, entity.getClient());
        assertEquals(employee, entity.getEmployee());
    }
}