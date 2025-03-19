package projekt.zespolowy.zero_waste.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Principal principal;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        when(response.getOutputStream()).thenReturn(outputStream);
        when(principal.getName()).thenReturn("testUser");
    }

    @Test
    void generateInvoice_Success() throws Exception {
        Long orderId = 1L;
        Product product = new Product();
        product.setId(1L);
        product.setName("Eco Bottle");
        product.setQuantity(2);
        product.setPrice(29.99);
        User owner = new User();
        owner.setUsername("ecoSeller");
        product.setOwner(owner);

        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setCreatedAt(LocalDateTime.now());

        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        orderController.generateInvoice(orderId.toString(), response, principal);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader(eq("Content-Disposition"), contains("invoice_" + orderId));
        verify(outputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
    }

    @Test
    void generateInvoice_OrderNotFound() {
        when(orderService.getOrderById(1L)).thenThrow(new EntityNotFoundException("Order not found"));

        assertThrows(EntityNotFoundException.class, () ->
                orderController.generateInvoice("1", response, principal)
        );
    }

    @Test
    void generateInvoice_ProductNotFound() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        Product product = new Product();
        product.setId(1L);
        order.setProduct(product);

        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        OrderDTO result = orderController.getOrderDTOByOrderId("1");
        assertNull(result);
    }
}
