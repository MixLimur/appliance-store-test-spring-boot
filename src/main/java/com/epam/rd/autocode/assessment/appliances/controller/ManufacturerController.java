package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping()
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String listManufacturers(Model model) {
        model.addAttribute("manufacturers", manufacturerService.getAll());
        return "manufacture/manufacturers";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showAddForm(Model model) {
        model.addAttribute("manufacturer", new CreateManufacturerRequest());
        return "manufacture/newManufacturer";
    }

    @PostMapping("/add-manufacturer")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addManufacturer(@Valid @ModelAttribute("manufacturer") CreateManufacturerRequest request,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "manufacture/newManufacturer";
        }
        manufacturerService.create(request);
        return "redirect:/manufacturers";
    }
}
