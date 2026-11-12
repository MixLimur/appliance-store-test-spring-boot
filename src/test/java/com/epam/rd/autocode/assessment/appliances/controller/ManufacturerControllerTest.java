package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.CreateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.ManufacturerResponse;
import com.epam.rd.autocode.assessment.appliances.dto.manufacturer.UpdateManufacturerRequest;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
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
class ManufacturerControllerTest {
    @Mock
    private ManufacturerService manufacturerService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ManufacturerController controller;

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
    @DisplayName("getAllManufacturers() should return page with asc sorting and flip reverseSortDir to desc")
    void getAllManufacturers_WithAscSort_ShouldReturnView() throws Exception {
        Page<ManufacturerResponse> page = new PageImpl<>(Collections.emptyList());
        when(manufacturerService.getAll(0, 5, "id", "asc", null)).thenReturn(page);

        mockMvc.perform(get("/manufacturers")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("manufacture/manufacturers"))
                .andExpect(model().attributeExists("manufacturers", "currentPage", "totalPages", "totalItems", "size", "sortField"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @DisplayName("getAllManufacturers() should return page with desc sorting and flip reverseSortDir to asc")
    void getAllManufacturers_WithDescSort_ShouldReturnView() throws Exception {
        Page<ManufacturerResponse> page = new PageImpl<>(Collections.emptyList());
        when(manufacturerService.getAll(0, 5, "name", "desc", "keyword")).thenReturn(page);

        mockMvc.perform(get("/manufacturers")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "name")
                        .param("sortDir", "desc")
                        .param("keyword", "keyword"))
                .andExpect(status().isOk())
                .andExpect(view().name("manufacture/manufacturers"))
                .andExpect(model().attribute("reverseSortDir", "asc"))
                .andExpect(model().attribute("keyword", "keyword"));
    }

    @Test
    @DisplayName("showAddForm() should return new manufacturer form")
    void showAddForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/manufacturers/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("manufacture/newManufacturer"))
                .andExpect(model().attributeExists("manufacturer"));
    }

    @Test
    @DisplayName("addManufacturer() should redirect to list on success")
    void addManufacturer_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/manufacturers/add-manufacturer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manufacturers"));

        verify(manufacturerService).create(any(CreateManufacturerRequest.class));
    }

    @Test
    @DisplayName("addManufacturer() should return form when validation fails")
    void addManufacturer_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.addManufacturer(new CreateManufacturerRequest(), bindingResult);

        assertEquals("manufacture/newManufacturer", view);
        verifyNoInteractions(manufacturerService);
    }

    @Test
    @DisplayName("showEditForm() should map fields and return edit form")
    void showEditForm_ShouldMapAndReturnView() throws Exception {
        ManufacturerResponse response = new ManufacturerResponse();
        response.setId(1L);
        response.setName("Test Manufacturer");

        when(manufacturerService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/manufacturers/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("manufacture/editManufacturer"))
                .andExpect(model().attributeExists("manufacturer"));
    }

    @Test
    @DisplayName("updateManufacturer() should redirect to list on success")
    void updateManufacturer_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/manufacturers/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manufacturers"));

        verify(manufacturerService).update(eq(1L), any(UpdateManufacturerRequest.class));
    }

    @Test
    @DisplayName("updateManufacturer() should return form when validation fails")
    void updateManufacturer_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        UpdateManufacturerRequest request = new UpdateManufacturerRequest();
        String view = controller.updateManufacturer(1L, request, bindingResult);

        assertEquals("manufacture/editManufacturer", view);
        assertEquals(1L, request.getId());
        verifyNoInteractions(manufacturerService);
    }

    @Test
    @DisplayName("deleteManufacturer() should redirect to list on success")
    void deleteManufacturer_WhenSuccess_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/manufacturers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manufacturers"))
                .andExpect(flash().attributeCount(0));

        verify(manufacturerService).delete(1L);
    }

    @Test
    @DisplayName("deleteManufacturer() should catch IllegalStateException and redirect with flash attribute")
    void deleteManufacturer_WhenReferenced_ShouldRedirectWithError() throws Exception {
        doThrow(new IllegalStateException("referenced")).when(manufacturerService).delete(1L);

        when(messageSource.getMessage(eq("error.manufacturer.delete.referenced"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete manufacturer.");

        mockMvc.perform(get("/manufacturers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manufacturers"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete manufacturer."));
    }

    @Test
    @DisplayName("deleteManufacturer() should catch ResourceNotFoundException and redirect with flash attribute")
    void deleteManufacturer_WhenNotFound_ShouldRedirectWithError() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(manufacturerService).delete(1L);

        when(messageSource.getMessage(eq("error.manufacturer.delete.referenced"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete manufacturer.");

        mockMvc.perform(get("/manufacturers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manufacturers"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete manufacturer."));
    }
}