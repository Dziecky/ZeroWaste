package projekt.zespolowy.zero_waste.services;

import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Konstruktorowe wstrzykiwanie zależności
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Implementacja metody z UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika: " + username);
        }
        return user;
    }

    // Zapisywanie nowego użytkownika po zakodowaniu hasła
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Ustawienie domyślnego typu konta na BUSINESS podczas produkcji
        user.setAccountType(AccountType.BUSINESS);
        userRepository.save(user);
    }

    // Znajdź użytkownika po nazwie użytkownika
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Znajdź użytkownika po emailu
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
