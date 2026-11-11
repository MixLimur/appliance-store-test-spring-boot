package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;

import java.util.List;

public interface ClientService {
    List<ClientResponse> getAll();
    ClientResponse getById(Long id);
    ClientResponse create(CreateClientRequest request);
    ClientResponse update(Long id, UpdateClientRequest request);
    void delete(Long id);
    ClientResponse getByEmail(String email);
}
