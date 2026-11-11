package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrdersMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ApplianceRepository applianceRepository;

    @Mock
    private OrdersMapper ordersMapper;

    @InjectMocks
    private OrderServiceImpl service;

    private Orders order;
    private CreateOrdersRequest createRequest;
    private UpdateOrdersRequest updateRequest;
    private OrdersResponse ordersResponse;
    private Client client;
    private Employee employee;
    private Appliance appliance;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);

        employee = new Employee();
        employee.setId(2L);

        appliance = new Appliance();
        appliance.setId(100L);

        order = new Orders();
        order.setId(10L);
        order.setClient(client);

        createRequest = new CreateOrdersRequest();
        createRequest.setClientId(1L);
        createRequest.setEmployeeId(2L);

        updateRequest = new UpdateOrdersRequest();
        updateRequest.setId(10L);
        updateRequest.setClientId(1L);
        updateRequest.setEmployeeId(2L);

        ClientResponse clientResponse = new ClientResponse(1L, "John", "john@test.com", "1234");
        EmployeeResponse employeeResponse = new EmployeeResponse(2L, "Jane", "jane@test.com", "Sales");

        ordersResponse = new OrdersResponse(10L, employeeResponse, clientResponse, Collections.emptySet(), true);
    }

    @Test
    @DisplayName("getAll() should return list of responses when orders exist")
    void getAll_WhenOrdersExist_ShouldReturnListOfResponses() {
        when(ordersRepository.findAll()).thenReturn(List.of(order));
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        List<OrdersResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ordersResponse, result.get(0));
        verify(ordersRepository).findAll();
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("getAll() should return empty list when no orders exist")
    void getAll_WhenEmpty_ShouldReturnEmptyList() {
        when(ordersRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrdersResponse> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ordersRepository).findAll();
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("getById() should return response when found")
    void getById_WhenFound_ShouldReturnResponse() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.getById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(ordersRepository).findById(10L);
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("getById() should throw exception when not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(ordersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
        verify(ordersRepository).findById(99L);
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("create() should save order when valid Client and Employee exist")
    void create_WhenValid_ShouldSaveAndReturnResponse() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(ordersMapper.toEntity(createRequest, client, employee, Collections.emptySet())).thenReturn(order);
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.create(createRequest);

        assertNotNull(result);
        verify(clientRepository).findById(1L);
        verify(employeeRepository).findById(2L);
        verify(ordersRepository).save(order);
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("create() should save order without employee when employeeId is null")
    void create_WhenEmployeeIdNull_ShouldSaveOrder() {
        createRequest.setEmployeeId(null);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ordersMapper.toEntity(createRequest, client, null, Collections.emptySet())).thenReturn(order);
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.create(createRequest);

        assertNotNull(result);
        verify(clientRepository).findById(1L);
        verify(employeeRepository, never()).findById(anyLong());
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("create() should throw exception when Client does not exist")
    void create_WhenClientNotFound_ShouldThrowException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createRequest));

        verify(ordersRepository, never()).save(any());
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("create() should throw exception when Employee does not exist")
    void create_WhenEmployeeNotFound_ShouldThrowException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createRequest));

        verify(ordersRepository, never()).save(any());
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("update() should update order and map rows when found and valid")
    void update_WhenValid_ShouldUpdateAndReturnResponse() {
        UpdateOrderRowRequest rowRequest = new UpdateOrderRowRequest();
        rowRequest.setApplianceId(100L);
        rowRequest.setNumber(2L);
        rowRequest.setAmount(new BigDecimal("200.00"));
        updateRequest.setOrderRowSet(Set.of(rowRequest));

        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(applianceRepository.findById(100L)).thenReturn(Optional.of(appliance));
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.update(10L, updateRequest);

        assertNotNull(result);
        verify(applianceRepository).findById(100L);
        verify(ordersMapper).updateEntityFromRequest(eq(updateRequest), eq(order), eq(client), eq(employee), anySet());
        verify(ordersRepository).save(order);
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("update() should throw exception when Appliance in row is not found")
    void update_WhenApplianceInRowNotFound_ShouldThrowException() {
        UpdateOrderRowRequest rowRequest = new UpdateOrderRowRequest();
        rowRequest.setApplianceId(999L);
        updateRequest.setOrderRowSet(Set.of(rowRequest));

        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(applianceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should update order without employee when employeeId is null")
    void update_WhenEmployeeIdNull_ShouldUpdate() {
        updateRequest.setEmployeeId(null);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.update(10L, updateRequest);

        assertNotNull(result);
        verify(employeeRepository, never()).findById(anyLong());
        verify(ordersMapper).updateEntityFromRequest(eq(updateRequest), eq(order), eq(client), eq(null), anySet());
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("update() should throw exception when Order not found")
    void update_WhenOrderNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));

        verify(clientRepository, never()).findById(anyLong());
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw exception when Client not found")
    void update_WhenClientNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));

        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw exception when Employee not found")
    void update_WhenEmployeeNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, updateRequest));

        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete order when found")
    void delete_WhenFound_ShouldDelete() {
        when(ordersRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(10L));

        verify(ordersRepository).existsById(10L);
        verify(ordersRepository).deleteById(10L);
    }

    @Test
    @DisplayName("delete() should throw exception when Order not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(ordersRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(ordersRepository).existsById(99L);
        verify(ordersRepository, never()).deleteById(anyLong());
    }
}