package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Override
    public ClientDto getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto create(ClientDto dto) {
        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + dto.getEmail());
        }

        Client client = clientMapper.toEntity(dto);
        client.setId(null);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);
    }

    @Override
    @Transactional
    public ClientDto update(Long id, ClientDto dto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        Optional<Client> clientWithSameEmail = clientRepository.findByEmail(dto.getEmail());
        if (clientWithSameEmail.isPresent() && !clientWithSameEmail.get().getId().equals(id)) {
            throw new DuplicateEmailException("Email is already in use by another client: " + dto.getEmail());
        }

        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());
        existingClient.setPassword(dto.getPassword());
        existingClient.setCard(dto.getCard());

        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toDto(updatedClient);
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
