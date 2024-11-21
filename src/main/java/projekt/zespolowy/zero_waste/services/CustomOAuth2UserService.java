package projekt.zespolowy.zero_waste.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("!!! CustomOAuth2UserService konstruktor");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws org.springframework.security.oauth2.core.OAuth2AuthenticationException {
        System.out.println("!!! loadUser wywołana");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Pobierz informacje o użytkowniku z Google
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String phoneNumber = null; // Google API nie udostępnia numeru telefonu w podstawowym zakresie

        // Generuj username: pierwsza litera imienia + do 6 liter nazwiska
        String username = generateUsername(firstName, lastName);

        // Sprawdź, czy użytkownik już istnieje
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Utwórz nowego użytkownika
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setAccountType(AccountType.NORMAL);

            // Hasło nie jest dostępne z Google, ustawiamy losowe hasło
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            try {
                userRepository.save(user);
                System.out.println("Utworzono nowego użytkownika: " + user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }

    private String generateUsername(String firstName, String lastName) {
        String firstInitial = firstName.substring(0, 1).toLowerCase();
        String lastNamePart = lastName.length() >= 6 ? lastName.substring(0, 6).toLowerCase() : lastName.toLowerCase();
        return firstInitial + lastNamePart;
    }
}