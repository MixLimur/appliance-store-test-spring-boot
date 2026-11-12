package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {
    Page<EmployeeResponse> getAll(int page, int size, String sortField, String sortDir, String keyword);
    EmployeeResponse getById(Long id);
    EmployeeResponse create(CreateEmployeeRequest dto);
    EmployeeResponse update(Long id, UpdateEmployeeRequest dto);
    void delete(Long id);
    EmployeeResponse getByEmail(String email);
}
