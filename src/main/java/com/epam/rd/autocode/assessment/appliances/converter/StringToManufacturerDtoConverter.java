package com.epam.rd.autocode.assessment.appliances.converter;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToManufacturerDtoConverter implements Converter<String, ManufacturerDto> {

    @Override
    public ManufacturerDto convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        ManufacturerDto dto = new ManufacturerDto();
        dto.setId(Long.valueOf(source));
        return dto;
    }

}
