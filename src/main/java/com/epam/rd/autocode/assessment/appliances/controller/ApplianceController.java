package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    @GetMapping()
    public String listAppliances(Model model) {
        model.addAttribute("appliances", applianceService.getAll());
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("appliance", new ApplianceDto());
        populateDropdowns(model);
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String addAppliance(@Valid @ModelAttribute("appliance") ApplianceDto applianceDto,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateDropdowns(model);
            return "appliance/newAppliance";
        }
        applianceService.create(applianceDto);
        return "redirect:/appliances";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        model.addAttribute("manufacturers", manufacturerService.getAll());
    }
}

