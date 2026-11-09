package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDto {

    private Long id;

    private EmployeeDto employee;

    @NotNull(message = "{validation.orders.client.notnull}")
    private ClientDto client;

    private Set<OrderRowDto> orderRowSet = new HashSet<>();

    private Boolean approved = false;

    public BigDecimal getAmount() {
        if (orderRowSet == null || orderRowSet.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderRowSet.stream()
                .map(OrderRowDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
