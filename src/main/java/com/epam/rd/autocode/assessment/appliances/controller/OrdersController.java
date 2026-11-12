package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.appliance.ApplianceResponse;
import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.OrderRowResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orderRow.UpdateOrderRowRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.CreateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.dto.orders.OrdersResponse;
import com.epam.rd.autocode.assessment.appliances.dto.orders.UpdateOrdersRequest;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private static final int DROPDOWN_PAGE_SIZE = 1000;
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_CLIENT = "ROLE_CLIENT";

    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ApplianceService applianceService;
    private final Validator validator;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Authentication authentication,
            Model model) {

        boolean isEmployee = hasRole(authentication, ROLE_EMPLOYEE);
        Page<OrdersResponse> pageResult = orderService.getAll(
                page, size, sortField, sortDir, keyword, authentication.getName(), isEmployee);

        populatePagingModel(model, pageResult, page, size, sortField, sortDir, keyword);
        return "order/orders";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String showAddForm(Model model, Authentication authentication) {
        CreateOrdersRequest request = new CreateOrdersRequest();
        if (hasRole(authentication, ROLE_CLIENT)) {
            ClientResponse currentClient = clientService.getByEmail(authentication.getName());
            request.setClientId(currentClient.getId());
        }
        populateOrderFormModel(model, authentication);
        model.addAttribute("order", request);
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String addOrder(@Valid @ModelAttribute("order") CreateOrdersRequest request,
                           BindingResult result,
                           Authentication authentication,
                           Model model) {
        resolveOrderParticipants(request, authentication);

        if (result.hasErrors()) {
            populateOrderFormModel(model, authentication);
            return "order/newOrder";
        }

        request.setApproved(false);
        orderService.create(request);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("error.orders.approved.delete"));
        }
        return "redirect:/orders";
    }

    @GetMapping("/{id}/approved")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String approveOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        OrdersResponse existingOrder = orderService.getById(id);
        UpdateOrdersRequest updateRequest = mapToUpdateRequest(existingOrder);
        updateRequest.setApproved(true);

        Set<ConstraintViolation<UpdateOrdersRequest>> violations = validator.validate(updateRequest);
        if (!violations.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", violations.iterator().next().getMessage());
            return "redirect:/orders";
        }

        orderService.update(id, updateRequest);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/unapproved")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String unapproveOrder(@PathVariable Long id) {
        setApprovalStatus(id, false);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String editOrder(@PathVariable Long id, Model model) {
        OrdersResponse order = orderService.getById(id);
        model.addAttribute("order", order);
        model.addAttribute("rows", order.getOrderRowSet());
        return "order/editOrder";
    }

    @GetMapping("/{id}/choice-appliance")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String showChoiceApplianceForm(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<ApplianceResponse> pageResult = applianceService.getAll(page, size, sortField, sortDir, keyword);

        model.addAttribute("ordersId", id);
        model.addAttribute("appliances", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        return "order/choiceAppliance";
    }

    @PostMapping("/add-into-order")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String addApplianceIntoOrder(@RequestParam Long ordersId,
                                        @RequestParam Long applianceId,
                                        @RequestParam Long numbers,
                                        @RequestParam BigDecimal price,
                                        RedirectAttributes redirectAttributes) {
        try {
            OrdersResponse existingOrder = orderService.getById(ordersId);
            UpdateOrdersRequest updateRequest = mapToUpdateRequest(existingOrder);
            updateRequest.getOrderRowSet().add(createOrderRowRequest(applianceId, numbers, price));
            orderService.update(ordersId, updateRequest);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("error.orders.approved.modify"));
        }
        return "redirect:/orders/" + ordersId + "/edit";
    }

    @GetMapping("/{id}/assign-self")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String assignSelfToOrder(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.assignEmployee(id, authentication.getName());
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("error.orders.approved.modify"));
        }
        return "redirect:/orders";
    }

    @GetMapping("/{orderId}/row/{rowId}/delete")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String deleteOrderRow(@PathVariable Long orderId,
                                 @PathVariable Long rowId,
                                 RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderRow(orderId, rowId);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("error.orders.approved.modify"));
        }
        return "redirect:/orders/" + orderId + "/edit";
    }

    @PostMapping("/{orderId}/row/{rowId}/update")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String updateOrderRowNumber(@PathVariable Long orderId,
                                       @PathVariable Long rowId,
                                       @RequestParam("number") Long number,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderRowNumber(orderId, rowId, number);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("error.orders.approved.modify"));
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", getLocalizedMessage("validation.orderrow.number.min"));
        }
        return "redirect:/orders/" + orderId + "/edit";
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private void populatePagingModel(Model model, Page<OrdersResponse> pageResult,
                                     int page, int size, String sortField, String sortDir, String keyword) {
        model.addAttribute("orders", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
    }

    private void populateOrderFormModel(Model model, Authentication authentication) {
        if (hasRole(authentication, ROLE_CLIENT)) {
            ClientResponse currentClient = clientService.getByEmail(authentication.getName());
            model.addAttribute("currentClientName", currentClient.getName());
        } else {
            model.addAttribute("clients", clientService.getAll(0, DROPDOWN_PAGE_SIZE, "id", "asc", null).getContent());
        }
    }

    private void resolveOrderParticipants(CreateOrdersRequest request, Authentication authentication) {
        if (hasRole(authentication, ROLE_CLIENT)) {
            ClientResponse currentClient = clientService.getByEmail(authentication.getName());
            request.setClientId(currentClient.getId());
            request.setEmployeeId(null);
        } else if (hasRole(authentication, ROLE_EMPLOYEE)) {
            EmployeeResponse currentEmployee = employeeService.getByEmail(authentication.getName());
            request.setEmployeeId(currentEmployee.getId());
        }
    }

    private void setApprovalStatus(Long id, boolean approved) {
        OrdersResponse existingOrder = orderService.getById(id);
        UpdateOrdersRequest updateRequest = mapToUpdateRequest(existingOrder);
        updateRequest.setApproved(approved);
        orderService.update(id, updateRequest);
    }

    private UpdateOrdersRequest mapToUpdateRequest(OrdersResponse response) {
        UpdateOrdersRequest request = new UpdateOrdersRequest();
        request.setId(response.getId());
        request.setClientId(response.getClient() != null ? response.getClient().getId() : null);
        request.setEmployeeId(response.getEmployee() != null ? response.getEmployee().getId() : null);
        request.setApproved(response.getApproved());
        request.setOrderRowSet(mapOrderRows(response.getOrderRowSet()));
        return request;
    }

    private Set<UpdateOrderRowRequest> mapOrderRows(Set<OrderRowResponse> rowSet) {
        if (rowSet == null) {
            return Collections.emptySet();
        }
        return rowSet.stream()
                .map(row -> {
                    UpdateOrderRowRequest rowRequest = new UpdateOrderRowRequest();
                    rowRequest.setId(row.getId());
                    rowRequest.setApplianceId(row.getAppliance() != null ? row.getAppliance().getId() : null);
                    rowRequest.setNumber(row.getNumber());
                    rowRequest.setAmount(row.getAmount());
                    return rowRequest;
                })
                .collect(Collectors.toSet());
    }

    private UpdateOrderRowRequest createOrderRowRequest(Long applianceId, Long numbers, BigDecimal price) {
        UpdateOrderRowRequest rowRequest = new UpdateOrderRowRequest();
        rowRequest.setApplianceId(applianceId);
        rowRequest.setNumber(numbers);
        rowRequest.setAmount(price.multiply(BigDecimal.valueOf(numbers)));
        return rowRequest;
    }

    private String getLocalizedMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
