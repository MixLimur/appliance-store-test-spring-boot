package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

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
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return "redirect:/employees";
    }

}
