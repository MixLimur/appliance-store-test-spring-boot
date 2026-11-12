package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    private Employee employee;
    private Client client;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("employee@test.com");
        employee.setPassword("encodedEmpPass");

        client = new Client();
        client.setId(2L);
        client.setEmail("client@test.com");
        client.setPassword("encodedClientPass");
    }

    @Test
    @DisplayName("loadUserByUsername() should return UserDetails with ROLE_EMPLOYEE when employee exists")
    void loadUserByUsername_WhenEmployeeExists_ShouldReturnEmployeeUserDetails() {
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("employee@test.com");

        assertNotNull(result);
        assertEquals("employee@test.com", result.getUsername());
        assertEquals("encodedEmpPass", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE")));

        verify(employeeRepository).findByEmail("employee@test.com");
        verifyNoInteractions(clientRepository);
    }

    @Test
    @DisplayName("loadUserByUsername() should return UserDetails with ROLE_CLIENT when employee not found and client exists")
    void loadUserByUsername_WhenEmployeeNotFoundAndClientExists_ShouldReturnClientUserDetails() {
        when(employeeRepository.findByEmail("client@test.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));

        UserDetails result = service.loadUserByUsername("client@test.com");

        assertNotNull(result);
        assertEquals("client@test.com", result.getUsername());
        assertEquals("encodedClientPass", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));

        verify(employeeRepository).findByEmail("client@test.com");
        verify(clientRepository).findByEmail("client@test.com");
    }

    @Test
    @DisplayName("loadUserByUsername() should throw UsernameNotFoundException when user is not found in both repositories")
    void loadUserByUsername_WhenUserNotFound_ShouldThrowUsernameNotFoundException() {
        when(employeeRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown@test.com")
        );

        assertEquals("User not found with email: unknown@test.com", exception.getMessage());
        verify(employeeRepository).findByEmail("unknown@test.com");
        verify(clientRepository).findByEmail("unknown@test.com");
    }

}