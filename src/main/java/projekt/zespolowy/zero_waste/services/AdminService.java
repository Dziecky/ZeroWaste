package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.OrderRepository;
import projekt.zespolowy.zero_waste.repository.ProductPriceHistoryRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.util.List;

@Service
public class AdminService {

    private final UserService userService;               // do pobrania zalogowanego admina
    private final UserRepository userRepository;         // do usunięcia
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository; // do usunięcia produktów
    private final OrderRepository orderRepository;
    private final ProductPriceHistoryRepository priceHistoryRepository;

    public AdminService(UserService userService,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        ProductPriceHistoryRepository priceHistoryRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Transactional
    public void deleteUserByAdmin(Long userId, String adminPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userService.findByUsernameAdmin(username);
        if (admin == null) {
            throw new IllegalArgumentException("Brak zalogowanego administratora");
        }
        // sprawdź hasło
        if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Błędne hasło administratora");
        }
        // usuń docelowego użytkownika
        User toDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));

        orderRepository.deleteByUser(toDelete);

        // 1) pobierz wszystkie produkty tego użytkownika
        List<Product> products = productRepository.findByOwner(toDelete);

        // 2) dla każdego produktu najpierw usuń zamówienia
        for (Product p : products) {
            orderRepository.deleteByProduct(p);
            priceHistoryRepository.deleteByProduct(p);
        }

        // 3) usuń produkty
        productRepository.deleteByOwner(toDelete);

        userRepository.deleteById(userId);
    }
}
