package projekt.zespolowy.zero_waste.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.dto.user.OrderSummaryDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Principal principal,
            Model model
    ) {

        User user = userService.findByUsername(principal.getName());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> ordersPage = orderService.getOrdersByUser(user, pageable);

        List<OrderSummaryDTO> orderDTOS = ordersPage.stream()
                .map(order -> productService.getProductById(order.getProduct().getId())
                        .map(product -> new OrderSummaryDTO(order.getId(), product.getPrice()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("orders", orderDTOS);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        return "orders/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String getOrderDetails(@PathVariable("orderId") String orderId, Model model) {
        Long id = Long.parseLong(orderId);
        Order order = orderService.getOrderById(id);
        Optional<Product> maybeProduct = productService.getProductById(order.getProduct().getId());
        OrderDTO orderDTO = null;
        if(maybeProduct.isPresent()) {
            Product product = maybeProduct.get();
            orderDTO = new OrderDTO(
                    order.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice(),
                    product.getImageUrl(),
                    product.getOwner().getUsername(),
                    order.getCreatedAt()
            );
        }
        model.addAttribute("order", orderDTO);
        return "orders/order-details";
    }
}
