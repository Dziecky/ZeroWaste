package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.OrderRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    public void testGetPurchasesByUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setUser(user);

        List<Order> expectedOrders = Collections.singletonList(order);
        when(orderRepository.findByUserId(userId)).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = statisticsService.getPurchasesByUser(userId);

        // Assert
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    public void testGetSalesByUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setOwner(user);

        Order order = new Order();
        order.setProduct(product);

        List<Order> allOrders = Collections.singletonList(order);
        when(orderRepository.findAll()).thenReturn(allOrders);

        List<Product> expectedProducts = Collections.singletonList(product);

        // Act
        List<Product> actualProducts = statisticsService.getSalesByUser(userId);

        // Assert
        assertEquals(expectedProducts, actualProducts);
    }
}