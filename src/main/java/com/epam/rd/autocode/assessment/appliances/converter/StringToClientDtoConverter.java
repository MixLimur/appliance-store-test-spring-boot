package com.epam.rd.autocode.assessment.appliances.converter;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToClientDtoConverter implements Converter<String, ClientDto> {

    @Override
    public ClientDto convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        ClientDto dto = new ClientDto();
        dto.setId(Long.valueOf(source));
        return dto;
    }

}
