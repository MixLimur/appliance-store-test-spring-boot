package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_CLIENT = "ROLE_CLIENT";

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    @Override
    @Loggable
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeRepository.findByEmail(email)
                .map(employee -> buildUserDetails(employee.getEmail(), employee.getPassword(), ROLE_EMPLOYEE))
                .or(() -> clientRepository.findByEmail(email)
                        .map(client -> buildUserDetails(client.getEmail(), client.getPassword(), ROLE_CLIENT)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private UserDetails buildUserDetails(String email, String password, String role) {
        return new User(
                email,
                password,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

}
