package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
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
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;
    private final MessageSource messageSource;

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

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteManufacturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            manufacturerService.delete(id);
        } catch (ResourceNotFoundException | IllegalStateException ex) {
            String localizedMessage = messageSource.getMessage(
                    "error.manufacturer.delete.referenced",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("errorMessage", localizedMessage);
        }
        return "redirect:/manufacturers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ManufacturerResponse manufacturer = manufacturerService.getById(id);

        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        request.setId(manufacturer.getId());
        request.setName(manufacturer.getName());

        model.addAttribute("manufacturer", request);
        return "manufacture/editManufacturer";
    }

    @PostMapping("/{id}/edit")
    public String updateManufacturer(@PathVariable Long id,
                                     @Valid @ModelAttribute("manufacturer") UpdateManufacturerRequest request,
                                     BindingResult result) {
        if (result.hasErrors()) {
            return "manufacture/editManufacturer";
        }
        manufacturerService.update(id, request);
        return "redirect:/manufacturers";
    }

}
