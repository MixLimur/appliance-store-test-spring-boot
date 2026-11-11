package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final MessageSource messageSource;

    @GetMapping()
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAll());
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

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeeResponse employee = employeeService.getById(id);

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setId(employee.getId());
        request.setName(employee.getName());
        request.setEmail(employee.getEmail());
        request.setDepartment(employee.getDepartment());

        model.addAttribute("employee", request);
        return "employee/editEmployee";
    }

    @PostMapping("/{id}/edit")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") UpdateEmployeeRequest request,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "employee/editEmployee";
        }
        employeeService.update(id, request);
        return "redirect:/employees";
    }

}
