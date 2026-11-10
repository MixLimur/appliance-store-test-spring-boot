package com.epam.rd.autocode.assessment.appliances.dto.manufacturer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateManufacturerRequest {

    @NotNull
    private Long id;

    @NotBlank(message = "{validation.manufacturer.name.notblank}")
    private String name;
}
