package com.epam.rd.autocode.assessment.appliances.dto.orders;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponse {

    private Long id;
    private EmployeeResponse employee;
    private ClientResponse client;
    private Set<OrderRowResponse> orderRowSet = new HashSet<>();
    private Boolean approved;

    public BigDecimal getAmount() {
        if (orderRowSet == null || orderRowSet.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderRowSet.stream()
                .map(OrderRowResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
