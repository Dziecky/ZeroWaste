package projekt.zespolowy.zero_waste.services.resetPassword;

public interface MailService {
    void sendPasswordResetEmail(String to, String resetLink);
}
