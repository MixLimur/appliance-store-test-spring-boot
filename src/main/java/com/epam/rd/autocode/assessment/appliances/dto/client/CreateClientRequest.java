package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.dto.user.CreateUserRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateClientRequest extends CreateUserRequest {

    @NotBlank(message = "{validation.client.card.notblank}")
    private String card;

}
