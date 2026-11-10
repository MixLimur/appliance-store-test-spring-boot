package com.epam.rd.autocode.assessment.appliances.dto.employee;

import com.epam.rd.autocode.assessment.appliances.dto.user.CreateUserRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateEmployeeRequest extends CreateUserRequest {

    @NotBlank(message = "{validation.employee.department.notblank}")
    private String department;

}
