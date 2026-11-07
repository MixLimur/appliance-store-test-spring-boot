package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDto getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public EmployeeDto create(EmployeeDto dto) {
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + dto.getEmail());
        }

        Employee employee = employeeMapper.toEntity(dto);
        employee.setId(null);

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDto update(Long id, EmployeeDto dto) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        Optional<Employee> employeeWithSameEmail = employeeRepository.findByEmail(dto.getEmail());
        if (employeeWithSameEmail.isPresent() && !employeeWithSameEmail.get().getId().equals(id)) {
            throw new DuplicateEmailException("Email is already in use by another employee: " + dto.getEmail());
        }

        existingEmployee.setName(dto.getName());
        existingEmployee.setEmail(dto.getEmail());
        existingEmployee.setPassword(dto.getPassword());
        existingEmployee.setDepartment(dto.getDepartment());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
