package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
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

    private Client entity;
    private ClientDto dto;

    @BeforeEach
    void setUp() {
        entity = new Client();
        entity.setId(1L);
        entity.setEmail("test@test.com");

        dto = new ClientDto();
        dto.setId(1L);
        dto.setEmail("test@test.com");
    }

    @Test
    @DisplayName("create() should throw DuplicateEmailException if email exists")
    void create_WhenEmailExists_ShouldThrowException() {
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));

        assertThrows(DuplicateEmailException.class, () -> service.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw DuplicateEmailException if email is taken by another user")
    void update_WhenEmailTakenByAnother_ShouldThrowException() {
        Client anotherClient = new Client();
        anotherClient.setId(2L); // ID відрізняється від нашого (1L)
        anotherClient.setEmail("test@test.com");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(anotherClient));

        assertThrows(DuplicateEmailException.class, () -> service.update(1L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update() should proceed if email belongs to the same user")
    void update_WhenEmailBelongsToSameUser_ShouldUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        // Знаходить сутність з ТИМ САМИМ ID
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        ClientDto result = service.update(1L, dto);

        assertNotNull(result);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("delete() should throw exception when client not found")
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(anyLong());
    }

}