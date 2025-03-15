package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.RefundService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private RefundService refundService;

    @Mock
    private Model model;

    @InjectMocks
    private RefundController refundController;

    private Order order;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("johndoe");

        order = new Order();
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setQuantity(10);
        product.setPrice(100.0);
        product.setImageUrl("http://image.url");
        product.setOwner(user);

        order.setProduct(product);
    }

    @Test
    void showRefundForm_OrderExists_ShouldReturnRefundView() {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        String viewName = refundController.showRefundForm("1", model);

        assertEquals("/orders/refund", viewName);
        verify(model).addAttribute(eq("order"), any(OrderDTO.class));
    }

    @Test
    void showRefundForm_orderNotFound_shouldRedirectToOrders() {
        when(orderService.getOrderById(1L)).thenReturn(null);

        String viewName = refundController.showRefundForm("1", model);

        assertEquals("redirect:/orders", viewName);
        verify(model).addAttribute("error", "Order not found.");
    }

    @Test
    void requestRefund_orderExists_shouldRedirectToOrdersView() {
        when(orderService.getOrderById(1L)).thenReturn(order);

        String viewName = refundController.requestRefund(1L, "Defective product", 50.0);

        assertEquals("redirect:/orders", viewName);
        verify(refundService).save(any(Refund.class));
    }

    @Test
    void requestRefund_orderNotFound_shouldRedirectToOrdersView() {
        when(orderService.getOrderById(1L)).thenReturn(null);

        String viewName = refundController.requestRefund(1L, "Defective product", 50.0);

        assertEquals("redirect:/orders", viewName);
        verify(refundService, never()).save(any(Refund.class));
    }
}
