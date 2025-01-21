package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(Principal principal, Model model) {

        User user = UserService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);

        model.addAttribute("orders", orders);

        return "orders/orders";
    }
}
