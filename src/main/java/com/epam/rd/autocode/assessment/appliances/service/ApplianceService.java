package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ApplianceService {
    Page<ApplianceResponse> getAll(int page, int size, String sortField, String sortDir, String keyword);
    ApplianceResponse getById(Long id);
    ApplianceResponse create(CreateApplianceRequest request);
    ApplianceResponse update(Long id, UpdateApplianceRequest request);
    void delete(Long id);
}
