package projekt.zespolowy.zero_waste.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.security.CustomUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOidcUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Wywołaj domyślną implementację, aby pobrać OidcUser
        OidcUser oidcUser = super.loadUser(userRequest);

        // Pobierz informacje o użytkowniku
        String email = oidcUser.getEmail();
        String firstName = oidcUser.getGivenName();
        String lastName = oidcUser.getFamilyName();

        // Generuj username
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
            user.setPhoneNumber(null); // Google domyślnie nie udostępnia numeru telefonu
            user.setAccountType(AccountType.BUSINESS); // Domyślnie ustaw typ konta na BUSINESS

            // Ustaw losowe hasło
            user.setPassword(passwordEncoder.encode(org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8)));

            // Zapisz użytkownika w bazie danych
            userRepository.save(user);
        }

        // Zwróć CustomUser z atrybutami OIDC
        return new CustomUser(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private String generateUsername(String firstName, String lastName) {
        String firstInitial = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1).toLowerCase() : "u";
        String lastNamePart = (lastName != null && !lastName.isEmpty())
                ? lastName.substring(0, Math.min(6, lastName.length())).toLowerCase()
                : "user";
        String randomNumber = org.apache.commons.lang3.RandomStringUtils.randomNumeric(4);
        return firstInitial + lastNamePart + randomNumber;
    }
}
