package com.epam.rd.autocode.assessment.appliances.validation;

import java.util.Collection;

public interface ApprovableOrder {
    Boolean getApproved();
    Collection<?> getOrderRowSet();
}
