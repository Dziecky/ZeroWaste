package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailReportServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailReportService emailReportService;

    private static final String TO = "test@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String REPORT_CONTENT = "Test Content";
    private static final String ARTICLE_TITLE = "New Article";
    private static final String CATEGORY_NAME = "Category A";
    private static final String ARTICLE_URL = "http://example.com/article";

    private Refund refund;
    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Tworzymy dane do testów refundacji i zamówienia
        user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("John");

        order = new Order();
        order.setId(123L);
        order.setUser(user);

        refund = new Refund();
        refund.setRefundAmount(100.0);
        refund.setRefundReason("Product damaged");
        refund.setStatus(RefundStatus.PENDING);
    }

    @Test
    void sendMonthlyReport_shouldSendEmail() {
        // given
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("k.przezdziecki.projekty@gmail.com");
        expectedMessage.setTo(TO);
        expectedMessage.setSubject(SUBJECT);
        expectedMessage.setText(REPORT_CONTENT);

        // when
        emailReportService.sendMonthlyReport(TO, SUBJECT, REPORT_CONTENT);

        // then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage capturedMessage = captor.getValue();

        assertEquals(expectedMessage.getFrom(), capturedMessage.getFrom());
        assertEquals(expectedMessage.getTo()[0], capturedMessage.getTo()[0]);
        assertEquals(expectedMessage.getSubject(), capturedMessage.getSubject());
        assertEquals(expectedMessage.getText(), capturedMessage.getText());
    }

    @Test
    void sendRefundAffirmation_shouldSendRefundEmail() {
        // given
        String expectedBody = String.format(
                "Dear %s,\n\n" +
                        "We have received your refund request for Order #%d.\n" +
                        "Refund Amount: $%.2f\n" +
                        "Reason: %s\n" +
                        "Status: %s\n\n" +
                        "Our team will review your request and get back to you shortly.\n\n" +
                        "Best regards,\n" +
                        "Zero Waste Team",
                user.getFirstName(),
                order.getId(),
                refund.getRefundAmount(),
                refund.getRefundReason(),
                refund.getStatus()
        );

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("admin@zero.waste");
        expectedMessage.setTo(user.getEmail());
        expectedMessage.setSubject("Refund Request Confirmation - Order #" + order.getId());
        expectedMessage.setText(expectedBody);

        // when
        emailReportService.sendRefundAffirmation(refund, order);

        // then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage capturedMessage = captor.getValue();

        assertEquals(expectedMessage.getFrom(), capturedMessage.getFrom());
        assertEquals(expectedMessage.getTo()[0], capturedMessage.getTo()[0]);
        assertEquals(expectedMessage.getSubject(), capturedMessage.getSubject());
        assertEquals(expectedMessage.getText(), capturedMessage.getText());
    }

    @Test
    void sendNewArticleNotification_shouldSendNewArticleEmail() {
        // given
        String expectedSubject = "Nowy artykuł w Twojej ulubionej kategorii: " + CATEGORY_NAME;
        String expectedContent = "Witaj!\n\n" +
                "Nowy artykuł \"" + ARTICLE_TITLE + "\" został dodany do Twojej ulubionej kategorii \"" + CATEGORY_NAME + "\".\n\n" +
                "Kliknij tutaj, aby przeczytać: " + ARTICLE_URL + "\n\n" +
                "Pozdrawiamy,\nZespół Zero Waste.";

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("k.przezdziecki.projekty@gmail.com");
        expectedMessage.setTo(TO);
        expectedMessage.setSubject(expectedSubject);
        expectedMessage.setText(expectedContent);

        // when
        emailReportService.sendNewArticleNotification(TO, ARTICLE_TITLE, CATEGORY_NAME, ARTICLE_URL);

        // then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage capturedMessage = captor.getValue();

        assertEquals(expectedMessage.getFrom(), capturedMessage.getFrom());
        assertEquals(expectedMessage.getTo()[0], capturedMessage.getTo()[0]);
        assertEquals(expectedMessage.getSubject(), capturedMessage.getSubject());
        assertEquals(expectedMessage.getText(), capturedMessage.getText());
    }
}
