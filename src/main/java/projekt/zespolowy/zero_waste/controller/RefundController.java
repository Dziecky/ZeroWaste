package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.RefundService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class RefundController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RefundService refundService;

    @GetMapping("/{orderId}/refund")
    public String showRefundForm(@PathVariable("orderId") String orderId, Model model) {
        OrderDTO order = getOrderDTOByOrderId(orderId);
        if (order == null) {
            model.addAttribute("error", "Order not found.");
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "/orders/refund";
    }

    @PostMapping("/{orderId}/refund")
    public String requestRefund(@PathVariable("orderId") Long orderId,
                                @RequestParam("refundReason") String refundReason,
                                @RequestParam("refundAmount") Double refundAmount) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/orders";
        }

        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setRefundReason(refundReason);
        refund.setRefundAmount(refundAmount);
        refund.setRequestDate(LocalDateTime.now());
        refund.setStatus(RefundStatus.PENDING);

        refundService.save(refund);
        return "redirect:/orders";
    }

    OrderDTO getOrderDTOByOrderId(String orderId) {
        Long id = Long.parseLong(orderId);
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return null;
        }
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
        return orderDTO;
    }
}
