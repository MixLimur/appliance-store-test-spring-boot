package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public ClientDto toDto(Client entity) {
        if (entity == null) {
            return null;
        }
        ClientDto dto = new ClientDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setCard(entity.getCard());
        return dto;
    }

    public Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setCard(dto.getCard());
        return entity;
    }
}
