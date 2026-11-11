package com.epam.rd.autocode.assessment.appliances.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OrderApprovalValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOrderApproval {

    String message() default "{validation.orders.approval.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
