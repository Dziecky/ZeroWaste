package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.OrderRepository;
import projekt.zespolowy.zero_waste.repository.ProductPriceHistoryRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductPriceHistoryRepository priceHistoryRepository;

    @InjectMocks private AdminService adminService;

    private static final String ADMIN_USERNAME = "adminUser";

    @BeforeEach
    void setUpSecurityContext() {
        // 1.Podstawiamy Authentication z nazwą admina
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(ADMIN_USERNAME);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void deleteUserByAdmin_success() {
        // given
        Long targetId = 42L;
        String rawAdminPass = "secret";

        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword("encodedAdminPwd");

        User toDelete = new User();
        toDelete.setId(targetId);

        Product p1 = new Product(); p1.setId(1L);
        Product p2 = new Product(); p2.setId(2L);
        List<Product> products = List.of(p1, p2);

        when(userService.findByUsernameAdmin(ADMIN_USERNAME)).thenReturn(admin);
        when(passwordEncoder.matches(rawAdminPass, "encodedAdminPwd")).thenReturn(true);
        when(userRepository.findById(targetId)).thenReturn(Optional.of(toDelete));
        when(productRepository.findByOwner(toDelete)).thenReturn(products);

        // when
        adminService.deleteUserByAdmin(targetId, rawAdminPass);

        // then – weryfikujemy sekwencję wywołań
        InOrder in = inOrder(orderRepository, priceHistoryRepository, productRepository, userRepository);

        // 1) usuń wszystkie zamówienia należące do użytkownika
        in.verify(orderRepository).deleteByUser(toDelete);

        // 2) usuń zamówienia i historię cen każdego produktu
        in.verify(orderRepository).deleteByProduct(p1);
        in.verify(priceHistoryRepository).deleteByProduct(p1);
        in.verify(orderRepository).deleteByProduct(p2);
        in.verify(priceHistoryRepository).deleteByProduct(p2);

        // 3) usuń produkty
        in.verify(productRepository).deleteByOwner(toDelete);

        // 4) usuń konto użytkownika
        in.verify(userRepository).deleteById(targetId);
    }

    @Test
    void deleteUserByAdmin_noLoggedAdmin_throws() {
        when(userService.findByUsernameAdmin(ADMIN_USERNAME)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminService.deleteUserByAdmin(1L, "pass")
        );
        assertEquals("Brak zalogowanego administratora", ex.getMessage());
        verifyNoInteractions(orderRepository, productRepository, userRepository);
    }

    @Test
    void deleteUserByAdmin_wrongPassword_throws() {
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword("encodedPwd");

        when(userService.findByUsernameAdmin(ADMIN_USERNAME)).thenReturn(admin);
        when(passwordEncoder.matches("badpass", "encodedPwd")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminService.deleteUserByAdmin(1L, "badpass")
        );
        assertEquals("Błędne hasło administratora", ex.getMessage());
        verify(orderRepository, never()).deleteByUser(any());
    }

    @Test
    void deleteUserByAdmin_targetNotFound_throws() {
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword("encodedPwd");

        when(userService.findByUsernameAdmin(ADMIN_USERNAME)).thenReturn(admin);
        when(passwordEncoder.matches("secret", "encodedPwd")).thenReturn(true);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminService.deleteUserByAdmin(99L, "secret")
        );
        assertEquals("Nie znaleziono użytkownika", ex.getMessage());
        verify(orderRepository, never()).deleteByUser(any());
    }
}
