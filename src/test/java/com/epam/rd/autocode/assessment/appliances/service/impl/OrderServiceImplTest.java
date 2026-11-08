package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrdersMapper;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
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

import java.util.List;
import java.util.Optional;

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
    private OrdersMapper ordersMapper;

    @InjectMocks
    private OrderServiceImpl service;

    private Orders order;
    private OrdersDto orderDto;
    private Client client;
    private Employee employee;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);

        employee = new Employee();
        employee.setId(2L);

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(2L);

        order = new Orders();
        order.setId(10L);
        order.setClient(client);

        orderDto = new OrdersDto();
        orderDto.setId(10L);
        orderDto.setClient(clientDto);
        orderDto.setEmployee(employeeDto); // Для деяких тестів ми включаємо працівника
    }

    @Test
    @DisplayName("getAll() should return list of OrdersDto")
    void getAll_ShouldReturnList() {
        when(ordersRepository.findAll()).thenReturn(List.of(order));
        when(ordersMapper.toDto(order)).thenReturn(orderDto);

        List<OrdersDto> result = service.getAll();

        assertEquals(1, result.size());
        verify(ordersRepository).findAll();
    }

    @Test
    @DisplayName("getById() should return DTO when found")
    void getById_WhenFound_ShouldReturnDto() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(ordersMapper.toDto(order)).thenReturn(orderDto);

        OrdersDto result = service.getById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("getById() should throw exception when not found")
    void getById_WhenNotFound_ShouldThrowException() {
        when(ordersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("create() should save order when valid Client and Employee exist")
    void create_WhenValid_ShouldSaveAndReturnDto() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(ordersMapper.toEntity(orderDto)).thenReturn(order);
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toDto(order)).thenReturn(orderDto);

        OrdersDto result = service.create(orderDto);

        assertNotNull(result);
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("create() should throw exception when Client does not exist")
    void create_WhenClientNotFound_ShouldThrowException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(orderDto));
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() should throw exception when Employee does not exist")
    void create_WhenEmployeeNotFound_ShouldThrowException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(orderDto));
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should update order when found and valid")
    void update_WhenValid_ShouldUpdate() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.of(order));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));

        when(ordersMapper.toEntity(orderDto)).thenReturn(order);
        when(ordersRepository.save(order)).thenReturn(order);
        when(ordersMapper.toDto(order)).thenReturn(orderDto);

        OrdersDto result = service.update(10L, orderDto);

        assertNotNull(result);
        verify(ordersRepository).save(order);
    }

    @Test
    @DisplayName("update() should throw exception when Order not found")
    void update_WhenOrderNotFound_ShouldThrowException() {
        when(ordersRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(10L, orderDto));
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete order when found")
    void delete_WhenFound_ShouldDelete() {
        when(ordersRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(ordersRepository).deleteById(10L);
    }

    @Test
    @DisplayName("delete() should throw exception when Order not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(ordersRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(ordersRepository, never()).deleteById(anyLong());
    }

}