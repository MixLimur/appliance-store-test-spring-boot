package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final PasswordEncoder passwordEncoder;

    public Employee toEntity(CreateEmployeeRequest request) {
        if (request == null) return null;

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setDepartment(request.getDepartment());
        return employee;
    }

    public void updateEntityFromRequest(UpdateEmployeeRequest request, Employee employee) {
        if (request == null || employee == null) return;

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
    }

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) return null;

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment()
        );
    }
}
