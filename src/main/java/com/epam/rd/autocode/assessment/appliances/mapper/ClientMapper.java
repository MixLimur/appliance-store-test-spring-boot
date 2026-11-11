package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final PasswordEncoder passwordEncoder;

    public Client toEntity(CreateClientRequest request) {
        if (request == null) return null;

        Client client = new Client();
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setCard(request.getCard());
        return client;
    }

    public void updateEntityFromRequest(UpdateClientRequest request, Client client) {
        if (request == null || client == null) return;

        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setCard(request.getCard());
    }

    public ClientResponse toResponse(Client client) {
        if (client == null) return null;

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getCard()
        );
    }


}
