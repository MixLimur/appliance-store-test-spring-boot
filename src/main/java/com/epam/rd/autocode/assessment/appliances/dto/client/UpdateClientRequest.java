package com.epam.rd.autocode.assessment.appliances.dto.client;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClientRequest {

    @NotNull
    private Long id;

    @NotBlank(message = "{validation.client.name.empty}")
    @Size(min = 2, max = 50, message = "{validation.client.name.size}")
    private String name;

    @NotBlank(message = "{validation.client.email.empty}")
    @Email(message = "{validation.client.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.client.card.empty}")
    private String card;

}
