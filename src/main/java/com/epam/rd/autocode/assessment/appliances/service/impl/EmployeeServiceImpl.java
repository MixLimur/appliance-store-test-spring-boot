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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public Page<EmployeeResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);
        Page<Employee> employeePage = fetchEmployeesPage(keyword, pageable);
        return employeePage.map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployeeById(id));
    }

    @Override
    public EmployeeResponse getByEmail(String email) {
        return employeeMapper.toResponse(findEmployeeByEmail(email));
    }

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        validateEmailUniquenessForCreate(request.getEmail());
        Employee employee = employeeMapper.toEntity(request);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = findEmployeeById(id);
        validateEmailUniquenessForUpdate(id, request.getEmail());

        employeeMapper.updateEntityFromRequest(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ensureEmployeeExists(id);
        try {
            employeeRepository.deleteById(id);
            employeeRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.employee.delete.referenced");
        }
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.ASC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Page<Employee> fetchEmployeesPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            String sanitizedKeyword = keyword.trim();
            return employeeRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                            sanitizedKeyword, sanitizedKeyword, sanitizedKeyword, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }

    private void validateEmailUniquenessForCreate(String email) {
        if (employeeRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + email);
        }
    }

    private void validateEmailUniquenessForUpdate(Long currentId, String email) {
        employeeRepository.findByEmail(email).ifPresent(existingEmployee -> {
            if (!existingEmployee.getId().equals(currentId)) {
                throw new DuplicateEmailException("Email " + email + " is already in use by another user");
            }
        });
    }

    private void ensureEmployeeExists(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
    }
}
