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
public class ManufacturerDto {

    private Long id;

    @NotBlank(message = "{validation.manufacturer.name.notblank}")
    private String name;
}
