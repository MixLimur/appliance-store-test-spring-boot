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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<ClientResponse> getAll(int page, int size, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);
        Page<Client> clientPage = fetchClientsPage(keyword, pageable);
        return clientPage.map(clientMapper::toResponse);
    }

    @Override
    public ClientResponse getById(Long id) {
        return clientMapper.toResponse(findClientById(id));
    }

    @Override
    public ClientResponse getByEmail(String email) {
        return clientMapper.toResponse(findClientByEmail(email));
    }

    @Override
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        validateEmailUniquenessForCreate(request.getEmail());
        Client client = clientMapper.toEntity(request);
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = findClientById(id);
        validateEmailUniquenessForUpdate(id, request.getEmail());

        clientMapper.updateEntityFromRequest(request, client);
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ensureClientExists(id);
        try {
            clientRepository.deleteById(id);
            clientRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("error.client.delete.referenced");
        }
    }

    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.ASC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Page<Client> fetchClientsPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            String sanitizedKeyword = keyword.trim();
            return clientRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    sanitizedKeyword, sanitizedKeyword, pageable);
        }
        return clientRepository.findAll(pageable);
    }

    private Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    private Client findClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with email: " + email));
    }

    private void validateEmailUniquenessForCreate(String email) {
        if (clientRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email is already in use: " + email);
        }
    }

    private void validateEmailUniquenessForUpdate(Long currentId, String email) {
        clientRepository.findByEmail(email).ifPresent(existingClient -> {
            if (!existingClient.getId().equals(currentId)) {
                throw new DuplicateEmailException("Email " + email + " is already in use by another user");
            }
        });
    }

    private void ensureClientExists(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
    }
}
