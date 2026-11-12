package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.CreateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.dto.appliance.UpdateApplianceRequest;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApplianceControllerTest {

    @Mock
    private ApplianceService applianceService;

    @Mock
    private ManufacturerService manufacturerService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ApplianceController controller;

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
    @DisplayName("getAllAppliances() should return page with asc sorting and flip reverseSortDir to desc")
    void getAllAppliances_WithAscSort_ShouldReturnView() throws Exception {
        Page<ApplianceResponse> page = new PageImpl<>(Collections.emptyList());
        when(applianceService.getAll(0, 5, "id", "asc", null)).thenReturn(page);

        mockMvc.perform(get("/appliances")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/appliances"))
                .andExpect(model().attributeExists("appliances", "currentPage", "totalPages", "totalItems", "size", "sortField"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @DisplayName("getAllAppliances() should return page with desc sorting and flip reverseSortDir to asc")
    void getAllAppliances_WithDescSort_ShouldReturnView() throws Exception {
        Page<ApplianceResponse> page = new PageImpl<>(Collections.emptyList());
        when(applianceService.getAll(0, 5, "name", "desc", "keyword")).thenReturn(page);

        mockMvc.perform(get("/appliances")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "name")
                        .param("sortDir", "desc")
                        .param("keyword", "keyword"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/appliances"))
                .andExpect(model().attribute("reverseSortDir", "asc"))
                .andExpect(model().attribute("keyword", "keyword"));
    }

    @Test
    @DisplayName("showAddForm() should return form and populate dropdowns")
    void showAddForm_ShouldReturnViewAndPopulateDropdowns() throws Exception {
        when(manufacturerService.getAll(eq(0), eq(1000), eq("name"), eq("asc"), isNull()))
                .thenReturn(new PageImpl(Collections.emptyList()));

        mockMvc.perform(get("/appliances/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/newAppliance"))
                .andExpect(model().attributeExists("appliance", "categories", "powerTypes", "manufacturers"));
    }

    @Test
    @DisplayName("addAppliance() should redirect to list on success")
    void addAppliance_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/appliances/add-appliance"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"));

        verify(applianceService).create(any(CreateApplianceRequest.class));
    }

    @Test
    @DisplayName("addAppliance() should return form when validation fails")
    void addAppliance_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model model = new ExtendedModelMap();

        when(manufacturerService.getAll(eq(0), eq(1000), eq("name"), eq("asc"), isNull()))
                .thenReturn(new PageImpl(Collections.emptyList()));

        String view = controller.addAppliance(new CreateApplianceRequest(), bindingResult, model);

        assertEquals("appliance/newAppliance", view);
        assertTrue(model.containsAttribute("categories"));
        assertTrue(model.containsAttribute("manufacturers"));
        verifyNoInteractions(applianceService);
    }

    @Test
    @DisplayName("showEditForm() should map fields when appliance has NO manufacturer")
    void showEditForm_WhenNoManufacturer_ShouldMapAndReturnView() throws Exception {
        ApplianceResponse response = new ApplianceResponse();
        response.setId(1L);
        response.setName("Test");
        response.setManufacturer(null);

        when(applianceService.getById(1L)).thenReturn(response);
        when(manufacturerService.getAll(eq(0), eq(1000), eq("name"), eq("asc"), isNull()))
                .thenReturn(new PageImpl(Collections.emptyList()));

        mockMvc.perform(get("/appliances/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/editAppliance"))
                .andExpect(model().attributeExists("appliance"));
    }

    @Test
    @DisplayName("showEditForm() should map manufacturerId when appliance HAS manufacturer")
    void showEditForm_WhenHasManufacturer_ShouldMapId() throws Exception {
        ApplianceResponse response = mock(ApplianceResponse.class, RETURNS_DEEP_STUBS);
        when(response.getId()).thenReturn(1L);
        when(response.getManufacturer().getId()).thenReturn(99L);

        when(applianceService.getById(1L)).thenReturn(response);
        when(manufacturerService.getAll(eq(0), eq(1000), eq("name"), eq("asc"), isNull()))
                .thenReturn(new PageImpl(Collections.emptyList()));

        mockMvc.perform(get("/appliances/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/editAppliance"));
    }

    @Test
    @DisplayName("updateAppliance() should redirect to list on success")
    void updateAppliance_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/appliances/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"));

        verify(applianceService).update(eq(1L), any(UpdateApplianceRequest.class));
    }

    @Test
    @DisplayName("updateAppliance() should return form when validation fails")
    void updateAppliance_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model model = new ExtendedModelMap();

        when(manufacturerService.getAll(eq(0), eq(1000), eq("name"), eq("asc"), isNull()))
                .thenReturn(new PageImpl(Collections.emptyList()));

        UpdateApplianceRequest request = new UpdateApplianceRequest();
        String view = controller.updateAppliance(1L, request, bindingResult, model);

        assertEquals("appliance/editAppliance", view);
        assertEquals(1L, request.getId());
        assertTrue(model.containsAttribute("categories"));
        verifyNoInteractions(applianceService);
    }

    @Test
    @DisplayName("deleteAppliance() should redirect to list on success")
    void deleteAppliance_WhenSuccess_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/appliances/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attributeCount(0));

        verify(applianceService).delete(1L);
    }

    @Test
    @DisplayName("deleteAppliance() should catch exception and redirect with flash attribute")
    void deleteAppliance_WhenReferenced_ShouldRedirectWithError() throws Exception {
        doThrow(new IllegalStateException("referenced")).when(applianceService).delete(1L);

        when(messageSource.getMessage(eq("error.appliance.delete.referenced"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete appliance because it is referenced in an order.");

        mockMvc.perform(get("/appliances/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete appliance because it is referenced in an order."));
    }

}