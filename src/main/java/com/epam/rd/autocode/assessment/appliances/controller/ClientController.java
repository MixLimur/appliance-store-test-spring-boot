package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAll());
        return "client/clients";
    }

    @GetMapping("/clients/add")
    public String showAddForm(Model model) {
        model.addAttribute("client", new ClientDto());
        return "client/newClient";
    }

    @PostMapping("/clients/add-client")
    public String addClient(@Valid @ModelAttribute("client") ClientDto clientDto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "client/newClient";
        }
        clientService.create(clientDto);
        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/delete")
    public String deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return "redirect:/clients";
    }

}
