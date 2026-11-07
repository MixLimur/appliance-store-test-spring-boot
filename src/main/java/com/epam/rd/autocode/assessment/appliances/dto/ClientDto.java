package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto extends UserDto {

    @NotBlank(message = "{validation.client.card.notblank}")
    private String card;

    public ClientDto(Long id, String name, String email, String password, String card) {
        super(id, name, email, password);
        this.card = card;
    }

}
