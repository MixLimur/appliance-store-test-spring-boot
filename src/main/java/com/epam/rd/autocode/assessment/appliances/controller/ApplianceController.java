package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;
    private final MessageSource messageSource;

    @GetMapping()
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String listAppliances(Model model) {
        model.addAttribute("appliances", applianceService.getAll());
        return "appliance/appliances";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showAddForm(Model model) {
        model.addAttribute("appliance", new CreateApplianceRequest());
        populateDropdowns(model);
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addAppliance(@Valid @ModelAttribute("appliance") CreateApplianceRequest request,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateDropdowns(model);
            return "appliance/newAppliance";
        }
        applianceService.create(request);
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteAppliance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applianceService.delete(id);
        } catch (IllegalStateException ex) {
            String localizedMessage = messageSource.getMessage(
                    "error.appliance.delete.referenced",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("errorMessage", localizedMessage);
        }
        return "redirect:/appliances";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        model.addAttribute("manufacturers", manufacturerService.getAll());
    }
}

