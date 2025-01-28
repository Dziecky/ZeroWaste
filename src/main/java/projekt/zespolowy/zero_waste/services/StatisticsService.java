package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.repository.OrderRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public List<Order> getPurchasesByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Product> getSalesByUser(Long userId) {
        List<Order> allOrders = orderRepository.findAll();
        List<Product> productsSoldByUser = new ArrayList<>();
        for (Order order : allOrders) {
            Product product = order.getProduct();

            if (product.getOwner().getId().equals(userId)) {
                productsSoldByUser.add(product);
            }
        }

        return productsSoldByUser;
    }


}
