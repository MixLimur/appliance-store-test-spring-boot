package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping()
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAll());
        return "client/clients";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showAddForm(Model model) {
        model.addAttribute("client", new CreateClientRequest());
        return "client/newClient";
    }

    @PostMapping("/add-client")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addClient(@Valid @ModelAttribute("client") CreateClientRequest request,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "client/newClient";
        }
        clientService.create(request);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return "redirect:/clients";
    }

}
