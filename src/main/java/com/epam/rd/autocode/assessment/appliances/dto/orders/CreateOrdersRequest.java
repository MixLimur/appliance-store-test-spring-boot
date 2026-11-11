package com.epam.rd.autocode.assessment.appliances.dto.orders;

import com.epam.rd.autocode.assessment.appliances.dto.orderRow.CreateOrderRowRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CreateOrdersRequest {

    @NotNull(message = "{validation.orders.client.notnull}")
    private Long clientId;

    private Long employeeId;

    private Set<CreateOrderRowRequest> orderRowSet = new HashSet<>();

    private Boolean approved = false;
}
