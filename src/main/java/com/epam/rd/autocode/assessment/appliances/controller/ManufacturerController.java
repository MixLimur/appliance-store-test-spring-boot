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
import org.springframework.data.domain.Page;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String getAllManufacturers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<ManufacturerResponse> pageResult = manufacturerService.getAll(page, size, sortField, sortDir, keyword);

        model.addAttribute("manufacturers", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

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
