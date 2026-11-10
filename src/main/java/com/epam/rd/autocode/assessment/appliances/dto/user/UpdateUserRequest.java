package com.epam.rd.autocode.assessment.appliances.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract class UpdateUserRequest {

    @NotNull
    private Long id;

    @NotBlank(message = "{validation.client.name.empty}")
    @Size(min = 2, max = 50, message = "{validation.client.name.size}")
    private String name;

    @NotBlank(message = "{validation.client.email.empty}")
    @Email(message = "{validation.client.email.invalid}")
    private String email;

}
