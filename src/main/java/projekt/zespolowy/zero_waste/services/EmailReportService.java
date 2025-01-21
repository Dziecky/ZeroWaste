package projekt.zespolowy.zero_waste.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}
