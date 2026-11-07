package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto extends UserDto {

    @NotBlank(message = "{validation.employee.department.notblank}")
    private String department;

    public EmployeeDto(Long id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
    }
}
