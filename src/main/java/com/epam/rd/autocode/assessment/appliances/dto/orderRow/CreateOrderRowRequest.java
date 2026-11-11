package com.epam.rd.autocode.assessment.appliances.dto.orderRow;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRowRequest {
    @NotNull(message = "{validation.orderrow.appliance.notnull}")
    private Long applianceId;

    @Min(value = 1, message = "{validation.orderrow.number.min}")
    @NotNull(message = "{validation.orderrow.number.notnull}")
    private Long number;

    @DecimalMin(value = "0.01", message = "{validation.orderrow.amount.min}")
    @NotNull(message = "{validation.orderrow.amount.notnull}")
    private BigDecimal amount;
}
