package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;

import java.util.List;

public interface ApplianceService {
    List<ApplianceResponse> getAll();
    ApplianceResponse getById(Long id);
    ApplianceResponse create(CreateApplianceRequest request);
    ApplianceResponse update(Long id, UpdateApplianceRequest request);
    void delete(Long id);
}
