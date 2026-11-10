package com.epam.rd.autocode.assessment.appliances.dto.manufacturer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateManufacturerRequest {

    @NotBlank(message = "{validation.manufacturer.name.notblank}")
    private String name;
}
