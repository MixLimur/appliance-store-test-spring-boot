package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.CreateClientRequest;
import com.epam.rd.autocode.assessment.appliances.dto.client.UpdateClientRequest;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {
    @Mock
    private ClientService clientService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ClientController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Validator mockValidator = new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(mockValidator)
                .build();
    }

    @Test
    @DisplayName("getAllClients() should return page with asc sorting and flip reverseSortDir to desc")
    void getAllClients_WithAscSort_ShouldReturnView() throws Exception {
        Page<ClientResponse> page = new PageImpl<>(Collections.emptyList());
        when(clientService.getAll(0, 5, "id", "asc", null)).thenReturn(page);

        mockMvc.perform(get("/clients")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/clients"))
                .andExpect(model().attributeExists("clients", "currentPage", "totalPages", "totalItems", "size", "sortField"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @DisplayName("getAllClients() should return page with desc sorting and flip reverseSortDir to asc")
    void getAllClients_WithDescSort_ShouldReturnView() throws Exception {
        Page<ClientResponse> page = new PageImpl<>(Collections.emptyList());
        when(clientService.getAll(0, 5, "name", "desc", "keyword")).thenReturn(page);

        mockMvc.perform(get("/clients")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "name")
                        .param("sortDir", "desc")
                        .param("keyword", "keyword"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/clients"))
                .andExpect(model().attribute("reverseSortDir", "asc"))
                .andExpect(model().attribute("keyword", "keyword"));
    }

    @Test
    @DisplayName("showAddForm() should return new client form")
    void showAddForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/clients/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/newClient"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    @DisplayName("addClient() should redirect to list on success")
    void addClient_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/clients/add-client"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        verify(clientService).create(any(CreateClientRequest.class));
    }

    @Test
    @DisplayName("addClient() should return form when validation fails")
    void addClient_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.addClient(new CreateClientRequest(), bindingResult);

        assertEquals("client/newClient", view);
        verifyNoInteractions(clientService);
    }

    @Test
    @DisplayName("showEditForm() should map fields and return edit form")
    void showEditForm_ShouldMapAndReturnView() throws Exception {
        ClientResponse response = new ClientResponse(1L,
                "Test Name",
                "test@email.com",
                "1234");

        when(clientService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/clients/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/editClient"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    @DisplayName("updateClient() should redirect to list on success")
    void updateClient_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/clients/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        verify(clientService).update(eq(1L), any(UpdateClientRequest.class));
    }

    @Test
    @DisplayName("updateClient() should return form when validation fails")
    void updateClient_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        UpdateClientRequest request = new UpdateClientRequest();
        String view = controller.updateClient(1L, request, bindingResult);

        assertEquals("client/editClient", view);
        assertEquals(1L, request.getId());
        verifyNoInteractions(clientService);
    }

    @Test
    @DisplayName("deleteClient() should redirect to list on success")
    void deleteClient_WhenSuccess_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/clients/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"))
                .andExpect(flash().attributeCount(0));

        verify(clientService).delete(1L);
    }

    @Test
    @DisplayName("deleteClient() should catch exception and redirect with flash attribute")
    void deleteClient_WhenReferenced_ShouldRedirectWithError() throws Exception {
        doThrow(new IllegalStateException("referenced")).when(clientService).delete(1L);

        when(messageSource.getMessage(eq("error.client.delete.referenced"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete client.");

        mockMvc.perform(get("/clients/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete client."));
    }

}