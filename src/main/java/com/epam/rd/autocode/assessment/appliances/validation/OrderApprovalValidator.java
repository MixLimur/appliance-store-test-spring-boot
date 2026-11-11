package com.epam.rd.autocode.assessment.appliances.validation;

import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class OrderApprovalValidator implements ConstraintValidator<ValidOrderApproval, ApprovableOrder> {

    @Override
    public boolean isValid(ApprovableOrder order, ConstraintValidatorContext context) {
        if (order == null) {
            return true;
        }

        if (Boolean.TRUE.equals(order.getApproved()) &&
                (order.getOrderRowSet() == null || order.getOrderRowSet().isEmpty())) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("orderRowSet")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
