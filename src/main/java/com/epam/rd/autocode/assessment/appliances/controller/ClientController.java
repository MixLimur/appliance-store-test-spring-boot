package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<ClientResponse> pageResult = clientService.getAll(page, size, sortField, sortDir, keyword);
        populatePagingModel(model, pageResult, page, size, sortField, sortDir, keyword);

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

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClientResponse client = clientService.getById(id);
        model.addAttribute("client", mapToUpdateRequest(client));
        return "client/editClient";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String updateClient(@PathVariable Long id,
                               @Valid @ModelAttribute("client") UpdateClientRequest request,
                               BindingResult result) {
        request.setId(id);
        if (result.hasErrors()) {
            return "client/editClient";
        }
        clientService.update(id, request);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.delete(id);
        } catch (IllegalStateException ex) {
            String localizedMessage = messageSource.getMessage(
                    "error.client.delete.referenced",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("errorMessage", localizedMessage);
        }
        return "redirect:/clients";
    }

    private void populatePagingModel(Model model, Page<ClientResponse> pageResult,
                                     int page, int size, String sortField, String sortDir, String keyword) {
        model.addAttribute("clients", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
    }

    private UpdateClientRequest mapToUpdateRequest(ClientResponse client) {
        UpdateClientRequest request = new UpdateClientRequest();
        request.setId(client.getId());
        request.setName(client.getName());
        request.setEmail(client.getEmail());
        request.setCard(client.getCard());
        return request;
    }

}
