package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ClientMapper;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    public ClientResponse getById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @Override
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + request.getEmail());
        }

        Client client = clientMapper.toEntity(request);
        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        clientRepository.findByEmail(request.getEmail()).ifPresent(existingClient -> {
            if (!existingClient.getId().equals(id)) {
                throw new DuplicateEmailException("Email " + request.getEmail() + " is already in use by another user");
            }
        });

        clientMapper.updateEntityFromRequest(request, client);

        Client updatedClient = clientRepository.save(client);


        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
