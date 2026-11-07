package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAll();
    ClientDto getById(Long id);
    ClientDto create(ClientDto dto);
    ClientDto update(Long id, ClientDto dto);
    void delete(Long id);
}
