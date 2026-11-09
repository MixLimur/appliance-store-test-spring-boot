package com.epam.rd.autocode.assessment.appliances.converter;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEmployeeDtoConverter implements Converter<String, EmployeeDto> {

    @Override
    public EmployeeDto convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        EmployeeDto dto = new EmployeeDto();
        dto.setId(Long.valueOf(source));
        return dto;
    }
}
