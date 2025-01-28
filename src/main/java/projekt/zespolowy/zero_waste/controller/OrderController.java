package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @GetMapping("/orders")
    public String viewOrders(Principal principal, Model model) {

        User user = UserService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);

        List<OrderDTO> orderDTOS = new ArrayList<>();

        for(Order order : orders) {
            Optional<Product> maybeProduct = productService.getProductById(order.getProduct().getId());
            if(maybeProduct.isPresent()) {
                Product product = maybeProduct.get();
                OrderDTO orderDTO = new OrderDTO(
                        order.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getImageUrl(),
                        product.getOwner().getUsername()
                );
                orderDTOS.add(orderDTO);
            }
        }
        System.out.println(orderDTOS);
        model.addAttribute("orders", orderDTOS);

        return "orders/orders";
    }
}
