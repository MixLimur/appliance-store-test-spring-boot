package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.employee.CreateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.UpdateEmployeeRequest;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
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
class EmployeeControllerTest {
    @Mock
    private EmployeeService employeeService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EmployeeController controller;

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
    @DisplayName("getAllEmployees() should return page with asc sorting and flip reverseSortDir to desc")
    void getAllEmployees_WithAscSort_ShouldReturnView() throws Exception {
        Page<EmployeeResponse> page = new PageImpl<>(Collections.emptyList());
        when(employeeService.getAll(0, 5, "id", "asc", null)).thenReturn(page);

        mockMvc.perform(get("/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employees"))
                .andExpect(model().attributeExists("employees", "currentPage", "totalPages", "totalItems", "size", "sortField"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @DisplayName("getAllEmployees() should return page with desc sorting and flip reverseSortDir to asc")
    void getAllEmployees_WithDescSort_ShouldReturnView() throws Exception {
        Page<EmployeeResponse> page = new PageImpl<>(Collections.emptyList());
        when(employeeService.getAll(0, 5, "name", "desc", "keyword")).thenReturn(page);

        mockMvc.perform(get("/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortField", "name")
                        .param("sortDir", "desc")
                        .param("keyword", "keyword"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/employees"))
                .andExpect(model().attribute("reverseSortDir", "asc"))
                .andExpect(model().attribute("keyword", "keyword"));
    }

    @Test
    @DisplayName("showAddForm() should return new employee form")
    void showAddForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/newEmployee"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    @DisplayName("addEmployee() should redirect to list on success")
    void addEmployee_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/employees/add-employee"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService).create(any(CreateEmployeeRequest.class));
    }

    @Test
    @DisplayName("addEmployee() should return form when validation fails")
    void addEmployee_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.addEmployee(new CreateEmployeeRequest(), bindingResult);

        assertEquals("employee/newEmployee", view);
        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("showEditForm() should map fields and return edit form")
    void showEditForm_ShouldMapAndReturnView() throws Exception {
        EmployeeResponse response = new EmployeeResponse(1L,
                "Test Name",
                "test@email.com",
                "security");

        when(employeeService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/employees/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/editEmployee"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    @DisplayName("updateEmployee() should redirect to list on success")
    void updateEmployee_WhenValid_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/employees/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService).update(eq(1L), any(UpdateEmployeeRequest.class));
    }

    @Test
    @DisplayName("updateEmployee() should return form when validation fails")
    void updateEmployee_WhenValidationFails_ShouldReturnForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        String view = controller.updateEmployee(1L, request, bindingResult);

        assertEquals("employee/editEmployee", view);
        assertEquals(1L, request.getId());
        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("deleteEmployee() should redirect to list on success")
    void deleteEmployee_WhenSuccess_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/employees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attributeCount(0));

        verify(employeeService).delete(1L);
    }

    @Test
    @DisplayName("deleteEmployee() should catch exception and redirect with flash attribute")
    void deleteEmployee_WhenReferenced_ShouldRedirectWithError() throws Exception {
        doThrow(new IllegalStateException("referenced")).when(employeeService).delete(1L);

        when(messageSource.getMessage(eq("error.employee.delete.referenced"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete employee.");

        mockMvc.perform(get("/employees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete employee."));
    }
}