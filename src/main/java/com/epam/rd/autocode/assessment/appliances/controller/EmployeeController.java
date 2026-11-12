package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<EmployeeResponse> pageResult = employeeService.getAll(page, size, sortField, sortDir, keyword);
        populatePagingModel(model, pageResult, page, size, sortField, sortDir, keyword);

        return "employee/employees";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new CreateEmployeeRequest());
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addEmployee(@Valid @ModelAttribute("employee") CreateEmployeeRequest request,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        employeeService.create(request);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeeResponse employee = employeeService.getById(id);
        model.addAttribute("employee", mapToUpdateRequest(employee));
        return "employee/editEmployee";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") UpdateEmployeeRequest request,
                                 BindingResult result) {
        request.setId(id);
        if (result.hasErrors()) {
            return "employee/editEmployee";
        }
        employeeService.update(id, request);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(id);
        } catch (IllegalStateException ex) {
            String localizedMessage = messageSource.getMessage(
                    "error.employee.delete.referenced",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("errorMessage", localizedMessage);
        }
        return "redirect:/employees";
    }

    private void populatePagingModel(Model model, Page<EmployeeResponse> pageResult,
                                     int page, int size, String sortField, String sortDir, String keyword) {
        model.addAttribute("employees", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
    }

    private UpdateEmployeeRequest mapToUpdateRequest(EmployeeResponse employee) {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setId(employee.getId());
        request.setName(employee.getName());
        request.setEmail(employee.getEmail());
        request.setDepartment(employee.getDepartment());
        return request;
    }
}
