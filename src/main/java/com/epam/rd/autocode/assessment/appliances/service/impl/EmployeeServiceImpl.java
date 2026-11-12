package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> employeePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            employeePage = employeeRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(k, k, k, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        return employeePage.map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + request.getEmail());
        }

        Employee employee = employeeMapper.toEntity(request);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employeeRepository.findByEmail(request.getEmail()).ifPresent(existingEmployee -> {
            if (!existingEmployee.getId().equals(id)) {
                throw new DuplicateEmailException("Email " + request.getEmail() + " is already in use by another user");
            }
        });

        employeeMapper.updateEntityFromRequest(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
        try {
            employeeRepository.deleteById(id);
            employeeRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.employee.delete.referenced");
        }
    }

    @Override
    public EmployeeResponse getByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }
}
