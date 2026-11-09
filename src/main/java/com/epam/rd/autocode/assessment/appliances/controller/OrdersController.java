package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ApplianceService applianceService;


    @GetMapping()
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAll());
        return "order/orders";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("order", new OrdersDto());
        model.addAttribute("clients", clientService.getAll());
        model.addAttribute("employees", employeeService.getAll());
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    public String addOrder(@ModelAttribute("order") OrdersDto ordersDto) {
        ordersDto.setApproved(false);
        orderService.create(ordersDto);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/approved")
    public String approveOrder(@PathVariable Long id) {
        OrdersDto order = orderService.getById(id);
        order.setApproved(true);
        orderService.update(id, order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/unapproved")
    public String unapproveOrder(@PathVariable Long id) {
        OrdersDto order = orderService.getById(id);
        order.setApproved(false);
        orderService.update(id, order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    public String editOrder(@PathVariable Long id, Model model) {
        OrdersDto order = orderService.getById(id);
        model.addAttribute("order", order);
        model.addAttribute("rows", order.getOrderRowSet());
        return "order/editOrder";
    }

    @GetMapping("/{id}/choice-appliance")
    public String showChoiceApplianceForm(@PathVariable Long id, Model model) {
        model.addAttribute("ordersId", id);
        model.addAttribute("appliances", applianceService.getAll());
        return "order/choiceAppliance";
    }

    @PostMapping("/add-into-order")
    public String addApplianceIntoOrder(@RequestParam Long ordersId,
                                        @RequestParam Long applianceId,
                                        @RequestParam Long numbers,
                                        @RequestParam BigDecimal price) {
        OrdersDto order = orderService.getById(ordersId);

        OrderRowDto rowDto = new OrderRowDto();
        rowDto.setAppliance(applianceService.getById(applianceId));
        rowDto.setNumber(numbers);
        rowDto.setAmount(price.multiply(BigDecimal.valueOf(numbers)));

        order.getOrderRowSet().add(rowDto);
        orderService.update(ordersId, order);

        return "redirect:/orders/" + ordersId + "/edit";
    }
}
