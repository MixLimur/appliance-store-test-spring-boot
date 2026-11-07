package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDto> getAll();
    EmployeeDto getById(Long id);
    EmployeeDto create(EmployeeDto dto);
    EmployeeDto update(Long id, EmployeeDto dto);
    void delete(Long id);
}
