package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepository extends JpaRepository<Appliance, Long> {

    Page<Appliance> findByNameContainingIgnoreCaseOrModelContainingIgnoreCaseOrManufacturerNameContainingIgnoreCase(
            String name, String model, String manufacturerName, Pageable pageable);
}
