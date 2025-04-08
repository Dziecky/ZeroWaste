package projekt.zespolowy.zero_waste.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;

@Service
public class EmailReportService {

    private final JavaMailSender mailSender;

    public EmailReportService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMonthlyReport(String to, String subject, String reportContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("k.przezdziecki.projekty@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(reportContent);
        mailSender.send(message);
    }

    public void sendRefundAffirmation(Refund refund, Order order) {
        String to = order.getUser().getEmail();
        String subject = "Refund Request Confirmation - Order #" + order.getId();
        String body = String.format(
                "Dear %s,\n\n" +
                        "We have received your refund request for Order #%d.\n" +
                        "Refund Amount: $%.2f\n" +
                        "Reason: %s\n" +
                        "Status: %s\n\n" +
                        "Our team will review your request and get back to you shortly.\n\n" +
                        "Best regards,\n" +
                        "Zero Waste Team",
                order.getUser().getFirstName(),
                order.getId(),
                refund.getRefundAmount(),
                refund.getRefundReason(),
                refund.getStatus()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@zero.waste");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
