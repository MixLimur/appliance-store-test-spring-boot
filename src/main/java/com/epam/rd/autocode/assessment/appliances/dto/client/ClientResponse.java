package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.dto.user.UserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClientResponse extends UserResponse {

    private String card;

    public ClientResponse(Long id, String name, String email, String card) {
        super(id, name, email);
        this.card = card;
    }
}
