package com.epam.rd.autocode.assessment.appliances.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class UserResponse {

    private Long id;
    private String name;
    private String email;
}
