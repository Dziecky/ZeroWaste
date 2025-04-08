package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.OrderStatsDTO;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderRepository orderRepository;

    public List<Order> getPurchasesByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Product> getSalesByUser(Long userId) {
        return orderRepository.findAll().stream()
                .map(Order::getProduct)
                .filter(product -> product.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<OrderStatsDTO> getQuarterlyOrderStats() {
        LocalDateTime quarterStart = LocalDateTime.now().minusMonths(3);
        List<Order> orders = orderRepository.findByCreatedAtAfter(quarterStart);

        return orders.stream()
                .filter(order -> order.getProduct() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getProduct().getProductCategory(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    ProductCategory category = entry.getKey();
                    List<Order> categoryOrders = entry.getValue();
                    Product sampleProduct = categoryOrders.get(0).getProduct();

                    return new OrderStatsDTO(
                            category,
                            categoryOrders.size(),
                            categoryOrders.stream()
                                    .mapToDouble(o -> o.getProduct().getQuantity())
                                    .sum(),
                            sampleProduct.getUnitOfMeasure().toString(),
                            categoryOrders.stream()
                                    .mapToDouble(o -> o.getProduct().getPrice() * o.getProduct().getQuantity())
                                    .sum()
                    );
                })
                .sorted(Comparator.comparing(OrderStatsDTO::getOrderCount).reversed())
                .collect(Collectors.toList());
    }
}