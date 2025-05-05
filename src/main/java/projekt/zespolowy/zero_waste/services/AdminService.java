package projekt.zespolowy.zero_waste.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.UserRepository;

@Service
public class AdminService {

    private final UserService userService;               // do pobrania zalogowanego admina
    private final UserRepository userRepository;         // do usunięcia
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserService userService,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteUserByAdmin(Long userId, String adminPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = UserService.findByUsername(username);
        if (admin == null) {
            throw new IllegalArgumentException("Brak zalogowanego administratora");
        }
        // sprawdź hasło
        if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Błędne hasło administratora");
        }
        // usuń docelowego użytkownika
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Nie znaleziono użytkownika");
        }
        userRepository.deleteById(userId);
    }
}
