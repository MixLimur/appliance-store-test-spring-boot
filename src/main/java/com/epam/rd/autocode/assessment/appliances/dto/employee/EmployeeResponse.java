package com.epam.rd.autocode.assessment.appliances.dto.employee;

import com.epam.rd.autocode.assessment.appliances.dto.user.UserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeResponse extends UserResponse {

    private String department;

    public EmployeeResponse(Long id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }
}
