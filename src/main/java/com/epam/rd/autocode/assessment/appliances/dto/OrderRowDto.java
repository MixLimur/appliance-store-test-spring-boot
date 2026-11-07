package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderRowDto {

    private Long id;

    @NotNull(message = "{validation.orderrow.appliance.notnull}")
    private ApplianceDto appliance;

    @Min(value = 1, message = "{validation.orderrow.number.min}")
    @NotNull(message = "{validation.orderrow.number.notnull}")
    private Long number;

    @DecimalMin(value = "0.01", message = "{validation.orderrow.amount.min}")
    @NotNull(message = "{validation.orderrow.amount.notnull}")
    private BigDecimal amount;
}
