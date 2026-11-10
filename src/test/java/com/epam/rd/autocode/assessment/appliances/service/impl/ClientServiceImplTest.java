package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ClientMapper;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientServiceImpl service;

    private Client clientEntity;
    private CreateClientRequest createRequest;
    private UpdateClientRequest updateRequest;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        clientEntity = new Client();
        clientEntity.setId(1L);
        clientEntity.setName("John Doe");
        clientEntity.setEmail("john.doe@example.com");
        clientEntity.setCard("1234-5678-9012-3456");

        createRequest = new CreateClientRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPassword("SecurePass123");
        createRequest.setCard("1234-5678-9012-3456");

        updateRequest = new UpdateClientRequest();
        updateRequest.setId(1L);
        updateRequest.setName("John Doe Updated");
        updateRequest.setEmail("john.doe@example.com");
        updateRequest.setCard("1234-5678-9012-3456");

        clientResponse = new ClientResponse(1L, "John Doe", "john.doe@example.com", "1234-5678-9012-3456");
    }

    @Test
    @DisplayName("getAll() should return list of client responses when clients exist")
    void getAll_WhenClientsExist_ShouldReturnListOfResponses() {
        when(repository.findAll()).thenReturn(List.of(clientEntity));
        when(mapper.toResponse(clientEntity)).thenReturn(clientResponse);

        List<ClientResponse> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clientResponse, result.get(0));
        verify(repository).findAll();
        verify(mapper).toResponse(clientEntity);
    }

    @Test
    @DisplayName("getAll() should return empty list when no clients exist")
    void getAll_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ClientResponse> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("getById() should return client response when client exists")
    void getById_WhenClientExists_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(mapper.toResponse(clientEntity)).thenReturn(clientResponse);

        ClientResponse result = service.getById(1L);

        assertNotNull(result);
        assertEquals(clientResponse, result);
        verify(repository).findById(1L);
        verify(mapper).toResponse(clientEntity);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when client does not exist")
    void getById_WhenClientNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
        verify(repository).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should throw DuplicateEmailException when email already exists")
    void create_WhenEmailExists_ShouldThrowException() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.of(clientEntity));

        assertThrows(DuplicateEmailException.class, () -> service.create(createRequest));
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("create() should save and return client response when valid request")
    void create_WhenValidRequest_ShouldSaveAndReturnResponse() {
        when(repository.findByEmail(createRequest.getEmail())).thenReturn(Optional.empty());
        when(mapper.toEntity(createRequest)).thenReturn(clientEntity);
        when(repository.save(clientEntity)).thenReturn(clientEntity);
        when(mapper.toResponse(clientEntity)).thenReturn(clientResponse);

        ClientResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(clientResponse, result);
        verify(repository).findByEmail(createRequest.getEmail());
        verify(mapper).toEntity(createRequest);
        verify(repository).save(clientEntity);
        verify(mapper).toResponse(clientEntity);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFoundException when client to update is not found")
    void update_WhenClientNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, updateRequest));
        verify(repository).findById(1L);
        verify(repository, never()).findByEmail(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw DuplicateEmailException when email is taken by another user")
    void update_WhenEmailTakenByAnotherUser_ShouldThrowException() {
        Client anotherClient = new Client();
        anotherClient.setId(2L);
        anotherClient.setEmail("john.doe@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(repository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(anotherClient));

        assertThrows(DuplicateEmailException.class, () -> service.update(1L, updateRequest));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should proceed when email belongs to the same user being updated")
    void update_WhenEmailBelongsToSameUser_ShouldUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(repository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(clientEntity));
        when(repository.save(clientEntity)).thenReturn(clientEntity);
        when(mapper.toResponse(clientEntity)).thenReturn(clientResponse);

        ClientResponse result = service.update(1L, updateRequest);

        assertNotNull(result);
        verify(mapper).updateEntityFromRequest(updateRequest, clientEntity);
        verify(repository).save(clientEntity);
        verify(mapper).toResponse(clientEntity);
    }

    @Test
    @DisplayName("update() should proceed when new email is not taken by anyone")
    void update_WhenEmailIsNewAndAvailable_ShouldUpdate() {
        updateRequest.setEmail("new.email@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(repository.findByEmail("new.email@example.com")).thenReturn(Optional.empty());
        when(repository.save(clientEntity)).thenReturn(clientEntity);
        when(mapper.toResponse(clientEntity)).thenReturn(clientResponse);

        ClientResponse result = service.update(1L, updateRequest);

        assertNotNull(result);
        verify(mapper).updateEntityFromRequest(updateRequest, clientEntity);
        verify(repository).save(clientEntity);
        verify(mapper).toResponse(clientEntity);
    }

    @Test
    @DisplayName("delete() should delete client when client exists")
    void delete_WhenClientExists_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when client does not exist")
    void delete_WhenClientNotFound_ShouldThrowException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        verify(repository).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }
}