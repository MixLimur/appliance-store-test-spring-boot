package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrdersMapper;
import com.epam.rd.autocode.assessment.appliances.model.*;
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
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

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
    private OrderRow orderRow;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);

        employee = new Employee();
        employee.setId(2L);
        employee.setEmail("employee@test.com");

        appliance = new Appliance();
        appliance.setId(100L);
        appliance.setPrice(new BigDecimal("150.00"));

        orderRow = new OrderRow();
        orderRow.setId(50L);
        orderRow.setNumber(2L);
        orderRow.setAppliance(appliance);
        orderRow.setAmount(new BigDecimal("300.00"));

        order = new Orders();
        order.setId(10L);
        order.setClient(client);
        order.setEmployee(employee);
        order.setApproved(false);
        order.setOrderRowSet(new HashSet<>(List.of(orderRow)));

        createRequest = new CreateOrdersRequest();
        createRequest.setClientId(1L);
        createRequest.setEmployeeId(2L);

        updateRequest = new UpdateOrdersRequest();
        updateRequest.setId(10L);
        updateRequest.setClientId(1L);
        updateRequest.setEmployeeId(2L);

        ClientResponse clientResponse = new ClientResponse(1L, "John", "john@test.com", "1234");
        EmployeeResponse employeeResponse = new EmployeeResponse(2L, "Jane", "jane@test.com", "Sales");

        ordersResponse = new OrdersResponse(10L, employeeResponse, clientResponse, Collections.emptySet(), false);
    }

    @Test
    @DisplayName("getAll() should fetch all orders with filter when user is employee")
    void getAll_WhenUserIsEmployee_ShouldUseFindAllWithFilter() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<Orders> page = new PageImpl<>(List.of(order));
        when(ordersRepository.findAllWithFilter("keyword", expectedPageable)).thenReturn(page);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        Page<OrdersResponse> result = service.getAll(0, 5, "id", "asc", "keyword", "emp@test.com", true);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ordersRepository).findAllWithFilter("keyword", expectedPageable);
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("getAll() should fetch client orders with filter when user is client")
    void getAll_WhenUserIsClient_ShouldUseFindByClientEmailWithFilter() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> page = new PageImpl<>(List.of(order));
        when(ordersRepository.findByClientEmailWithFilter("client@test.com", "keyword", expectedPageable)).thenReturn(page);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        Page<OrdersResponse> result = service.getAll(0, 5, "id", "desc", "keyword", "client@test.com", false);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ordersRepository).findByClientEmailWithFilter("client@test.com", "keyword", expectedPageable);
        verify(ordersMapper).toResponse(order);
    }

    @Test
    @DisplayName("getAll() should sort content ascending by amount when sortField is amount and sortDir is asc")
    void getAll_WhenSortFieldIsAmountAndAsc_ShouldSortContentAscending() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Orders order2 = new Orders();
        order2.setId(11L);

        OrdersResponse response1 = mock(OrdersResponse.class);
        when(response1.getAmount()).thenReturn(new BigDecimal("100.00"));

        OrdersResponse response2 = mock(OrdersResponse.class);
        when(response2.getAmount()).thenReturn(new BigDecimal("50.00"));

        Page<Orders> page = new PageImpl<>(List.of(order, order2));
        when(ordersRepository.findAllWithFilter(null, expectedPageable)).thenReturn(page);
        when(ordersMapper.toResponse(order)).thenReturn(response1);
        when(ordersMapper.toResponse(order2)).thenReturn(response2);

        Page<OrdersResponse> result = service.getAll(0, 5, "amount", "asc", null, "emp@test.com", true);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(new BigDecimal("50.00"), result.getContent().get(0).getAmount());
        assertEquals(new BigDecimal("100.00"), result.getContent().get(1).getAmount());
    }

    @Test
    @DisplayName("getAll() should sort content descending by amount when sortField is amount and sortDir is desc")
    void getAll_WhenSortFieldIsAmountAndDesc_ShouldSortContentDescending() {
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        Orders order2 = new Orders();
        order2.setId(11L);

        OrdersResponse response1 = mock(OrdersResponse.class);
        when(response1.getAmount()).thenReturn(new BigDecimal("50.00"));

        OrdersResponse response2 = mock(OrdersResponse.class);
        when(response2.getAmount()).thenReturn(new BigDecimal("100.00"));

        Page<Orders> page = new PageImpl<>(List.of(order, order2));
        when(ordersRepository.findAllWithFilter(null, expectedPageable)).thenReturn(page);
        when(ordersMapper.toResponse(order)).thenReturn(response1);
        when(ordersMapper.toResponse(order2)).thenReturn(response2);

        Page<OrdersResponse> result = service.getAll(0, 5, "amount", "desc", null, "emp@test.com", true);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(new BigDecimal("100.00"), result.getContent().get(0).getAmount());
        assertEquals(new BigDecimal("50.00"), result.getContent().get(1).getAmount());
    }

    @Test
    @DisplayName("getById() should return response when order is found")
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
    @DisplayName("getById() should throw ResourceNotFoundException when order is not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(ordersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));

        verify(ordersRepository).findById(99L);
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("create() should save order when valid client and employee exist")
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
    @DisplayName("create() should throw ResourceNotFoundException when client does not exist")
    void create_WhenClientNotFound_ShouldThrowException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createRequest));

        verify(ordersRepository, never()).save(any());
        verifyNoInteractions(ordersMapper);
    }

    @Test
    @DisplayName("create() should throw ResourceNotFoundException when employee does not exist")
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
    @DisplayName("update() should update order with empty rows when orderRowSet is null")
    void update_WhenOrderRowSetIsNull_ShouldUpdateWithEmptyRows() {
        updateRequest.setOrderRowSet(null);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toResponse(order)).thenReturn(ordersResponse);

        OrdersResponse result = service.update(10L, updateRequest);

        assertNotNull(result);
        verify(ordersMapper).updateEntityFromRequest(eq(updateRequest), eq(order), eq(client), eq(employee), eq(Collections.emptySet()));
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when appliance in row is not found")
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
    @DisplayName("update() should throw IllegalStateException when order is approved")
    void update_WhenOrderApproved_ShouldThrowIllegalStateException() {
        order.setApproved(true);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.update(10L, updateRequest));
        assertEquals("Cannot modify an approved order with id: 10", ex.getMessage());

        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete order when found and not approved")
    void delete_WhenFound_ShouldDelete() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> service.delete(10L));

        verify(ordersRepository).findById(10L);
        verify(ordersRepository).deleteById(10L);
    }

    @Test
    @DisplayName("delete() should throw IllegalStateException when order is approved")
    void delete_WhenOrderApproved_ShouldThrowIllegalStateException() {
        order.setApproved(true);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.delete(10L));
        assertEquals("Cannot modify an approved order with id: 10", ex.getMessage());

        verify(ordersRepository).findById(10L);
        verify(ordersRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("assignEmployee() should assign employee and save order when valid")
    void assignEmployee_WhenValid_ShouldAssignAndSave() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> service.assignEmployee(10L, "employee@test.com"));

        assertEquals(employee, order.getEmployee());
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("assignEmployee() should throw ResourceNotFoundException when employee is not found")
    void assignEmployee_WhenEmployeeNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("absent@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.assignEmployee(10L, "absent@test.com"));

        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignEmployee() should throw IllegalStateException when order is approved")
    void assignEmployee_WhenOrderApproved_ShouldThrowIllegalStateException() {
        order.setApproved(true);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> service.assignEmployee(10L, "employee@test.com"));

        verify(employeeRepository, never()).findByEmail(any());
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteOrderRow() should remove row and save order when valid")
    void deleteOrderRow_WhenValid_ShouldRemoveRowAndSave() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> service.deleteOrderRow(10L, 50L));

        assertTrue(order.getOrderRowSet().isEmpty());
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("deleteOrderRow() should throw IllegalStateException when order is approved")
    void deleteOrderRow_WhenOrderApproved_ShouldThrowIllegalStateException() {
        order.setApproved(true);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> service.deleteOrderRow(10L, 50L));

        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateOrderRowNumber() should update quantity and amount when valid")
    void updateOrderRowNumber_WhenValid_ShouldUpdateQuantityAndAmount() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        service.updateOrderRowNumber(10L, 50L, 3L);

        assertEquals(3L, orderRow.getNumber());
        assertEquals(new BigDecimal("450.00"), orderRow.getAmount());
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("updateOrderRowNumber() should keep amount unchanged when appliance or price is null")
    void updateOrderRowNumber_WhenApplianceOrPriceIsNull_ShouldNotRecalculateAmount() {
        orderRow.setAppliance(null);
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        service.updateOrderRowNumber(10L, 50L, 4L);
        assertEquals(4L, orderRow.getNumber());
        assertEquals(new BigDecimal("300.00"), orderRow.getAmount());

        Appliance applianceWithoutPrice = new Appliance();
        orderRow.setAppliance(applianceWithoutPrice);

        service.updateOrderRowNumber(10L, 50L, 5L);
        assertEquals(5L, orderRow.getNumber());
        assertEquals(new BigDecimal("300.00"), orderRow.getAmount());
    }

    @Test
    @DisplayName("updateOrderRowNumber() should throw IllegalArgumentException when number is less than 1")
    void updateOrderRowNumber_WhenNumberLessThanOne_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.updateOrderRowNumber(10L, 50L, 0L));
        assertThrows(IllegalArgumentException.class, () -> service.updateOrderRowNumber(10L, 50L, null));

        verify(ordersRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateOrderRowNumber() should throw ResourceNotFoundException when row is not found")
    void updateOrderRowNumber_WhenRowNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(ResourceNotFoundException.class, () -> service.updateOrderRowNumber(10L, 999L, 3L));

        verify(ordersRepository, never()).save(any());
    }
}