package com.epam.rd.autocode.assessment.appliances.dto.appliance;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateApplianceRequest {
    @NotNull
    private Long id;

    @NotBlank(message = "{validation.appliance.name.notblank}")
    private String name;

    @NotNull(message = "{validation.appliance.category.notnull}")
    private Category category;

    @NotBlank(message = "{validation.appliance.model.notblank}")
    private String model;

    @NotNull(message = "{validation.appliance.manufacturer.notnull}")
    private Long manufacturerId;

    @NotNull(message = "{validation.appliance.powerType.notnull}")
    private PowerType powerType;

    private String characteristic;

    private String description;

    @Min(value = 1, message = "{validation.appliance.power.min}")
    private Integer power;

    @DecimalMin(value = "0.01", message = "{validation.appliance.price.min}")
    @NotNull(message = "{validation.appliance.price.notnull}")
    private BigDecimal price;
}
