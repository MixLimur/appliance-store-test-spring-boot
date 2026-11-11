package com.epam.rd.autocode.assessment.appliances.dto.orderRow;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRowResponse {

    private Long id;
    private ApplianceResponse appliance;
    private Long number;
    private BigDecimal amount;
}
