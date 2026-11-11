package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.client.ClientResponse;
import com.epam.rd.autocode.assessment.appliances.dto.employee.EmployeeResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ApplianceService applianceService;
    private final Validator validator;
    private final MessageSource messageSource;


    @GetMapping()
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAll());
        return "order/orders";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String showAddForm(Model model, Authentication authentication) {
        CreateOrdersRequest request = new CreateOrdersRequest();

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isClient) {
            ClientResponse currentClient = clientService.getByEmail(authentication.getName());
            request.setClientId(currentClient.getId());
            model.addAttribute("currentClientName", currentClient.getName());
        } else {
            model.addAttribute("clients", clientService.getAll());
        }

        model.addAttribute("order", request);
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String addOrder(@Valid @ModelAttribute("order") CreateOrdersRequest request, BindingResult result,
                           Authentication authentication,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            return "order/newOrder";
        }

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isClient) {
            ClientResponse currentClient = clientService.getByEmail(authentication.getName());
            request.setClientId(currentClient.getId());
            request.setEmployeeId(null);
        } else if (isEmployee) {
            EmployeeResponse currentEmployee = employeeService.getByEmail(authentication.getName());
            request.setEmployeeId(currentEmployee.getId());
        }

        if (result.hasErrors()) {
            if (!isClient) {
                model.addAttribute("clients", clientService.getAll());
            } else {
                ClientResponse currentClient = clientService.getByEmail(authentication.getName());
                model.addAttribute("currentClientName", currentClient.getName());
            }
            return "order/newOrder";
        }

        request.setApproved(false);
        orderService.create(request);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
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
            String errorMessage = violations.iterator().next().getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
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
    public String showChoiceApplianceForm(@PathVariable Long id, Model model) {
        model.addAttribute("ordersId", id);
        model.addAttribute("appliances", applianceService.getAll());
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

            UpdateOrderRowRequest newRow = new UpdateOrderRowRequest();
            newRow.setApplianceId(applianceId);
            newRow.setNumber(numbers);
            newRow.setAmount(price.multiply(BigDecimal.valueOf(numbers)));

            updateRequest.getOrderRowSet().add(newRow);
            orderService.update(ordersId, updateRequest);
        } catch (IllegalStateException ex) {
            String localizedMessage = messageSource.getMessage(
                    "error.orders.approved.modify",
                    null,
                    LocaleContextHolder.getLocale()
            );
            redirectAttributes.addFlashAttribute("errorMessage", localizedMessage);
        }

        return "redirect:/orders/" + ordersId + "/edit";
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

        if (response.getOrderRowSet() != null) {
            Set<UpdateOrderRowRequest> rows = response.getOrderRowSet().stream()
                    .map(row -> {
                        UpdateOrderRowRequest r = new UpdateOrderRowRequest();
                        r.setId(row.getId());
                        r.setApplianceId(row.getAppliance() != null ? row.getAppliance().getId() : null);
                        r.setNumber(row.getNumber());
                        r.setAmount(row.getAmount());
                        return r;
                    })
                    .collect(Collectors.toSet());
            request.setOrderRowSet(rows);
        }

        return request;
    }

    @GetMapping("/{id}/assign-self")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String assignSelfToOrder(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String currentEmployeeEmail = authentication.getName();
            orderService.assignEmployee(id, currentEmployeeEmail);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
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
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
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
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/orders/" + orderId + "/edit";
    }
}
