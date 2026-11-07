package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "{validation.user.name.notblank}")
    private String name;

    @Email(message = "{validation.user.email.invalid}")
    @NotBlank(message = "{validation.user.email.notblank}")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "{validation.user.password.complexity}")
    @NotBlank(message = "{validation.user.password.notblank}")
    private String password;
}
