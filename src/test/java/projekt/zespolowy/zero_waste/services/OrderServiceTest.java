package projekt.zespolowy.zero_waste.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(10L);

        order = new Order(user, product);
        order.setId(100L);
    }

    @Test
    void create_shouldSaveAndReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.create(user, product);

        assertNotNull(createdOrder);
        assertEquals(order.getId(), createdOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenExists() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(100L);

        assertNotNull(foundOrder);
        assertEquals(100L, foundOrder.getId());
        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    void getOrderById_shouldReturnNull_whenNotExists() {
        when(orderRepository.findById(200L)).thenReturn(Optional.empty());

        Order foundOrder = orderService.getOrderById(200L);

        assertNull(foundOrder);
        verify(orderRepository, times(1)).findById(200L);
    }

    @Test
    void getOrdersByUser_shouldReturnOrdersList() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByUserId(1L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUser(user);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getOrdersByUser_shouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        Order order2 = new Order(user, product);
        order2.setId(101L);

        Page<Order> mockPage = new PageImpl<>(List.of(order2, order));

        when(orderRepository.findByUser(user, pageable)).thenReturn(mockPage);

        Page<Order> result = orderService.getOrdersByUser(user, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        assertEquals(101L, result.getContent().get(0).getId());
        assertEquals(100L, result.getContent().get(1).getId());

        verify(orderRepository, times(1)).findByUser(user, pageable);
    }
}
