package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.dto.user.UpdateUserRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateClientRequest extends UpdateUserRequest {

    @NotBlank(message = "{validation.client.card.notblank}")
    private String card;

}
