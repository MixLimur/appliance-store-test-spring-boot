package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock private OrderService orderService;
    @Mock private ClientService clientService;
    @Mock private EmployeeService employeeService;
    @Mock private ApplianceService applianceService;
    @Mock private Validator jakartaValidator;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private OrdersController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        org.springframework.validation.Validator mockSpringValidator = new org.springframework.validation.Validator() {
            @Override public boolean supports(Class<?> clazz) { return true; }
            @Override public void validate(Object target, Errors errors) { }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(mockSpringValidator)
                .build();
    }

    private Authentication getMockAuth(String role, String username) {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn(username);
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        lenient().doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();
        return auth;
    }

    @Test
    @DisplayName("getAllOrders() should return page with desc sorting for client role")
    void getAllOrders_ClientRole_DescSort() throws Exception {
        Authentication auth = getMockAuth("ROLE_CLIENT", "client@test.com");
        Page<OrdersResponse> page = new PageImpl<>(Collections.emptyList());
        when(orderService.getAll(0, 5, "id", "desc", null, "client@test.com", false)).thenReturn(page);

        mockMvc.perform(get("/orders")
                        .principal(auth)
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/orders"))
                .andExpect(model().attribute("reverseSortDir", "asc"));
    }

    @Test
    @DisplayName("getAllOrders() should return page with asc sorting for employee role")
    void getAllOrders_EmployeeRole_AscSort() throws Exception {
        Authentication auth = getMockAuth("ROLE_EMPLOYEE", "emp@test.com");
        Page<OrdersResponse> page = new PageImpl<>(Collections.emptyList());
        when(orderService.getAll(0, 5, "id", "asc", "key", "emp@test.com", true)).thenReturn(page);

        mockMvc.perform(get("/orders")
                        .principal(auth)
                        .param("sortDir", "asc")
                        .param("keyword", "key"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/orders"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @DisplayName("showAddForm() should set current client name for client role")
    void showAddForm_Client() throws Exception {
        Authentication auth = getMockAuth("ROLE_CLIENT", "client@test.com");

        ClientResponse client = mock(ClientResponse.class);
        when(client.getId()).thenReturn(10L);
        when(client.getName()).thenReturn("John Doe");

        when(clientService.getByEmail("client@test.com")).thenReturn(client);

        mockMvc.perform(get("/orders/add").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("order/newOrder"))
                .andExpect(model().attribute("currentClientName", "John Doe"));
    }

    @Test
    @DisplayName("showAddForm() should populate clients list for employee role")
    void showAddForm_Employee() throws Exception {
        Authentication auth = getMockAuth("ROLE_EMPLOYEE", "emp@test.com");
        when(clientService.getAll(eq(0), eq(1000), eq("id"), eq("asc"), isNull()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/orders/add").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("order/newOrder"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    @DisplayName("addOrder() should redirect to list on success for employee")
    void addOrder_Employee_Success() throws Exception {
        Authentication auth = getMockAuth("ROLE_EMPLOYEE", "emp@test.com");

        EmployeeResponse employee = mock(EmployeeResponse.class);
        when(employee.getId()).thenReturn(99L);

        when(employeeService.getByEmail("emp@test.com")).thenReturn(employee);

        mockMvc.perform(post("/orders/add-order").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).create(any(CreateOrdersRequest.class));
    }

    @Test
    @DisplayName("addOrder() should redirect to list on success for client")
    void addOrder_Client_Success() throws Exception {
        Authentication auth = getMockAuth("ROLE_CLIENT", "client@test.com");

        ClientResponse client = mock(ClientResponse.class);
        when(client.getId()).thenReturn(10L);

        when(clientService.getByEmail("client@test.com")).thenReturn(client);

        mockMvc.perform(post("/orders/add-order").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).create(any(CreateOrdersRequest.class));
    }

    @Test
    @DisplayName("addOrder() should return form when validation fails")
    void addOrder_ValidationFails() {
        Authentication auth = getMockAuth("ROLE_CLIENT", "client@test.com");

        ClientResponse client = mock(ClientResponse.class);
        when(client.getId()).thenReturn(10L);

        when(clientService.getByEmail("client@test.com")).thenReturn(client);

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        Model model = new ExtendedModelMap();

        String view = controller.addOrder(new CreateOrdersRequest(), result, auth, model);

        assertEquals("order/newOrder", view);
        verifyNoInteractions(orderService);
    }

    @Test
    @DisplayName("deleteOrder() should redirect to list on success")
    void deleteOrder_Success() throws Exception {
        mockMvc.perform(get("/orders/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));
        verify(orderService).delete(1L);
    }

    @Test
    @DisplayName("deleteOrder() should catch IllegalStateException and redirect with flash attribute")
    void deleteOrder_ThrowsException() throws Exception {
        doThrow(new IllegalStateException()).when(orderService).delete(1L);
        when(messageSource.getMessage(eq("error.orders.approved.delete"), isNull(), any(Locale.class)))
                .thenReturn("Cannot delete order.");

        mockMvc.perform(get("/orders/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Cannot delete order."));
    }

    @Test
    @DisplayName("approveOrder() should redirect to list on success")
    void approveOrder_Success() throws Exception {
        OrdersResponse order = mock(OrdersResponse.class);
        when(order.getId()).thenReturn(1L);
        when(order.getApproved()).thenReturn(false);

        ClientResponse client = mock(ClientResponse.class);
        when(client.getId()).thenReturn(10L);
        when(order.getClient()).thenReturn(client);

        EmployeeResponse employee = mock(EmployeeResponse.class);
        when(employee.getId()).thenReturn(20L);
        when(order.getEmployee()).thenReturn(employee);

        OrderRowResponse row = mock(OrderRowResponse.class);
        when(row.getId()).thenReturn(100L);
        when(row.getNumber()).thenReturn(5L);
        when(row.getAmount()).thenReturn(BigDecimal.TEN);

        ApplianceResponse appliance = mock(ApplianceResponse.class);
        when(appliance.getId()).thenReturn(50L);
        when(row.getAppliance()).thenReturn(appliance);

        when(order.getOrderRowSet()).thenReturn(new HashSet<>(Collections.singletonList(row)));

        when(orderService.getById(1L)).thenReturn(order);
        when(jakartaValidator.validate(any())).thenReturn(Collections.emptySet());

        mockMvc.perform(get("/orders/1/approved"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).update(eq(1L), any(UpdateOrdersRequest.class));
    }

    @Test
    @DisplayName("approveOrder() should redirect with flash attribute when validation fails")
    void approveOrder_ValidationFails() throws Exception {
        OrdersResponse order = mock(OrdersResponse.class);
        when(order.getClient()).thenReturn(null);
        when(order.getEmployee()).thenReturn(null);
        when(order.getOrderRowSet()).thenReturn(null);
        when(orderService.getById(1L)).thenReturn(order);

        ConstraintViolation<UpdateOrdersRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Validation Error");

        when(jakartaValidator.validate(any(UpdateOrdersRequest.class)))
                .thenReturn((Set) Collections.singleton(violation));

        mockMvc.perform(get("/orders/1/approved"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Validation Error"));
    }

    @Test
    @DisplayName("unapproveOrder() should redirect to list on success")
    void unapproveOrder_Success() throws Exception {
        OrdersResponse order = mock(OrdersResponse.class);
        when(order.getId()).thenReturn(1L);
        when(order.getApproved()).thenReturn(true);
        when(order.getClient()).thenReturn(null);
        when(order.getEmployee()).thenReturn(null);
        when(order.getOrderRowSet()).thenReturn(null);

        when(orderService.getById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1/unapproved"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).update(eq(1L), any(UpdateOrdersRequest.class));
    }

    @Test
    @DisplayName("editOrder() should return edit form and map fields")
    void editOrder_Success() throws Exception {
        OrdersResponse order = mock(OrdersResponse.class);
        when(order.getOrderRowSet()).thenReturn(Collections.emptySet());
        when(orderService.getById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/editOrder"))
                .andExpect(model().attributeExists("order", "rows"));
    }

    @Test
    @DisplayName("showChoiceApplianceForm() should return choice form")
    void showChoiceApplianceForm_Success() throws Exception {
        Page<ApplianceResponse> page = new PageImpl<>(Collections.emptyList());
        when(applianceService.getAll(0, 5, "id", "asc", null)).thenReturn(page);

        mockMvc.perform(get("/orders/1/choice-appliance"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/choiceAppliance"))
                .andExpect(model().attributeExists("ordersId", "appliances", "currentPage"));
    }

    @Test
    @DisplayName("addApplianceIntoOrder() should redirect to edit form on success")
    void addApplianceIntoOrder_Success() throws Exception {
        OrdersResponse order = mock(OrdersResponse.class);
        when(order.getClient()).thenReturn(null);
        when(order.getEmployee()).thenReturn(null);
        when(order.getApproved()).thenReturn(false);
        when(order.getOrderRowSet()).thenReturn(new HashSet<>());

        when(orderService.getById(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/add-into-order")
                        .param("ordersId", "1")
                        .param("applianceId", "10")
                        .param("numbers", "2")
                        .param("price", "100.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1/edit"));

        verify(orderService).update(eq(1L), any(UpdateOrdersRequest.class));
    }

    @Test
    @DisplayName("addApplianceIntoOrder() should catch exception and redirect with flash attribute")
    void addApplianceIntoOrder_ThrowsException() throws Exception {
        when(orderService.getById(1L)).thenThrow(new IllegalStateException());
        when(messageSource.getMessage(eq("error.orders.approved.modify"), isNull(), any(Locale.class)))
                .thenReturn("Cannot modify order.");

        mockMvc.perform(post("/orders/add-into-order")
                        .param("ordersId", "1")
                        .param("applianceId", "10")
                        .param("numbers", "2")
                        .param("price", "100.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Cannot modify order."));
    }

    @Test
    @DisplayName("assignSelfToOrder() should redirect to list on success")
    void assignSelfToOrder_Success() throws Exception {
        Authentication auth = getMockAuth("ROLE_EMPLOYEE", "emp@test.com");

        mockMvc.perform(get("/orders/1/assign-self").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).assignEmployee(1L, "emp@test.com");
    }

    @Test
    @DisplayName("deleteOrderRow() should redirect to edit form on success")
    void deleteOrderRow_Success() throws Exception {
        mockMvc.perform(get("/orders/1/row/10/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1/edit"));
        verify(orderService).deleteOrderRow(1L, 10L);
    }

    @Test
    @DisplayName("updateOrderRowNumber() should redirect to edit form on success")
    void updateOrderRowNumber_Success() throws Exception {
        mockMvc.perform(post("/orders/1/row/10/update")
                        .param("number", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1/edit"));
        verify(orderService).updateOrderRowNumber(1L, 10L, 5L);
    }

    @Test
    @DisplayName("updateOrderRowNumber() should catch IllegalArgumentException and redirect with flash attribute")
    void updateOrderRowNumber_ThrowsIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException()).when(orderService).updateOrderRowNumber(1L, 10L, 0L);
        when(messageSource.getMessage(eq("validation.orderrow.number.min"), isNull(), any(Locale.class)))
                .thenReturn("Number must be > 0");

        mockMvc.perform(post("/orders/1/row/10/update").param("number", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Number must be > 0"));
    }

    @Test
    @DisplayName("assignSelfToOrder() should catch IllegalStateException and redirect with flash attribute")
    void assignSelfToOrder_ThrowsException() throws Exception {
        Authentication auth = getMockAuth("ROLE_EMPLOYEE", "emp@test.com");

        doThrow(new IllegalStateException()).when(orderService).assignEmployee(1L, "emp@test.com");
        when(messageSource.getMessage(eq("error.orders.approved.modify"), isNull(), any(Locale.class)))
                .thenReturn("Cannot modify order.");

        mockMvc.perform(get("/orders/1/assign-self").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Cannot modify order."));
    }

    @Test
    @DisplayName("deleteOrderRow() should catch IllegalStateException and redirect with flash attribute")
    void deleteOrderRow_ThrowsException() throws Exception {
        doThrow(new IllegalStateException()).when(orderService).deleteOrderRow(1L, 10L);
        when(messageSource.getMessage(eq("error.orders.approved.modify"), isNull(), any(Locale.class)))
                .thenReturn("Cannot modify order.");

        mockMvc.perform(get("/orders/1/row/10/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Cannot modify order."));
    }

    @Test
    @DisplayName("updateOrderRowNumber() should catch IllegalStateException and redirect with flash attribute")
    void updateOrderRowNumber_ThrowsIllegalState() throws Exception {
        doThrow(new IllegalStateException()).when(orderService).updateOrderRowNumber(1L, 10L, 5L);
        when(messageSource.getMessage(eq("error.orders.approved.modify"), isNull(), any(Locale.class)))
                .thenReturn("Cannot modify order.");

        mockMvc.perform(post("/orders/1/row/10/update").param("number", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Cannot modify order."));
    }


}