//package projekt.zespolowy.zero_waste.services;
//
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import projekt.zespolowy.zero_waste.entity.User;
//import projekt.zespolowy.zero_waste.repository.UserRepository;
//
//@Service
//public class UserServiceImpl implements UserService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public User registerUser(User user) {
//        // Sprawdzenie unikalności nazwy użytkownika i email
//        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new RuntimeException("Nazwa użytkownika już istnieje");
//        }
//        if (userRepository.existsByEmail(user.getEmail())) {
//            throw new RuntimeException("Email już istnieje");
//        }
//        // Szyfrowanie hasła
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User loginUser(String username, String password) {
//        User user = userRepository.findByUsername(username);
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            return user;
//        } else {
//            throw new RuntimeException("Nieprawidłowa nazwa użytkownika lub hasło");
//        }
//    }
//}
