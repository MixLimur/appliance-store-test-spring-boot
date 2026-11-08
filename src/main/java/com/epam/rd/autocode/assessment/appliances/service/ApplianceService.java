package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface ApplianceService {
    List<ApplianceDto> getAll();
    ApplianceDto getById(Long id);
    ApplianceDto create(ApplianceDto dto);
    ApplianceDto update(Long id, ApplianceDto dto);
    void delete(Long id);
}
