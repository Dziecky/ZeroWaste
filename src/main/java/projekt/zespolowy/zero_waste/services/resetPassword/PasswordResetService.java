package projekt.zespolowy.zero_waste.services.resetPassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.PasswordResetToken;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.PasswordResetTokenRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.url}")
    private String appUrl;

    public void createAndSendResetToken(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        passwordResetTokenRepository.findByUser(user).ifPresent(oldToken -> passwordResetTokenRepository.delete(oldToken));

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetToken.setUsed(false);

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = appUrl + "/reset-password?token=" + token;
        mailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public User validateTokenAndGetUser(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElse(null);
        if (passwordResetToken == null) {
            return null;
        }

        if (passwordResetToken.isExpired()) {
            return null;
        }

        if (passwordResetToken.isUsed()) {
            return null;
        }

        return passwordResetToken.getUser();
    }

    public void markTokenAsUsed(String token) {
        passwordResetTokenRepository.findByToken(token).ifPresent(t -> {
            t.setUsed(true);
            passwordResetTokenRepository.save(t);
        });
    }
}
