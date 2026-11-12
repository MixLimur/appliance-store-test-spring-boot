package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String getAllAppliances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<ApplianceResponse> pageResult = applianceService.getAll(page, size, sortField, sortDir, keyword);

        model.addAttribute("appliances", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

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

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ApplianceResponse appliance = applianceService.getById(id);

        UpdateApplianceRequest request = new UpdateApplianceRequest();
        request.setId(appliance.getId());
        request.setName(appliance.getName());
        request.setCategory(appliance.getCategory());
        request.setModel(appliance.getModel());
        request.setPrice(appliance.getPrice());
        request.setPower(appliance.getPower());
        request.setPowerType(appliance.getPowerType());
        request.setDescription(appliance.getDescription());
        if (appliance.getManufacturer() != null) {
            request.setManufacturerId(appliance.getManufacturer().getId());
        }

        model.addAttribute("appliance", request);
        model.addAttribute("manufacturers", manufacturerService.getAll(0, 1000, "name", "asc", null).getContent());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        return "appliance/editAppliance";
    }

    @PostMapping("/{id}/edit")
    public String updateAppliance(@PathVariable Long id,
                                  @Valid @ModelAttribute("appliance") UpdateApplianceRequest request,
                                  BindingResult result,
                                  Model model) {
        request.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("manufacturers",manufacturerService.getAll(0, 1000, "name", "asc", null).getContent());
            model.addAttribute("categories", Category.values());
            model.addAttribute("powerTypes", PowerType.values());
            return "appliance/editAppliance";
        }
        applianceService.update(id, request);
        return "redirect:/appliances";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        model.addAttribute("manufacturers", manufacturerService.getAll(0, 1000, "name", "asc", null).getContent());
    }
}

