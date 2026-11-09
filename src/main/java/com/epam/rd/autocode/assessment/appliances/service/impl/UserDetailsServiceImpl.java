package com.epam.rd.autocode.assessment.appliances.service.impl;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            return new User(
                    employee.get().getEmail(),
                    employee.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
            );
        }

        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return new User(
                    client.get().getEmail(),
                    client.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
