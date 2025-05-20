package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.entity.enums.ReportStatus;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;
import projekt.zespolowy.zero_waste.repository.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock private ReportRepository reportRepo;
    @Mock private UserRepository userRepo;
    @Mock private ProductRepository productRepo;
    @Mock private ProductService productService;
    @InjectMocks private ReportService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReport_savesWithCorrectFields() {
        User reporter = new User();
        service.createReport(ReportType.PRODUCT, 10L, reporter, "reason");
        ArgumentCaptor<Report> cap = ArgumentCaptor.forClass(Report.class);
        verify(reportRepo).save(cap.capture());
        Report saved = cap.getValue();
        assertThat(saved.getType()).isEqualTo(ReportType.PRODUCT);
        assertThat(saved.getTargetId()).isEqualTo(10L);
        assertThat(saved.getReporter()).isSameAs(reporter);
        assertThat(saved.getReason()).isEqualTo("reason");
    }

    @Test
    void getNewReports_delegatesToRepo() {
        PageRequest pr = PageRequest.of(2, 7);
        when(reportRepo.findByStatus(ReportStatus.NEW, pr)).thenReturn(Page.empty());
        Page<Report> result = service.getNewReports(2, 7);
        assertThat(result).isEmpty();
        verify(reportRepo).findByStatus(ReportStatus.NEW, pr);
    }

    @Test
    void blockUser_success() {
        Report r = new Report();
        r.setType(ReportType.USER);
        r.setTargetId(55L);
        when(reportRepo.findById(1L)).thenReturn(Optional.of(r));
        User u = new User();
        when(userRepo.findById(55L)).thenReturn(Optional.of(u));

        service.blockUser(1L);

        assertThat(u.isEnabled()).isFalse();
        assertThat(r.getStatus()).isEqualTo(ReportStatus.RESOLVED);

        // zapis użytkownika
        verify(userRepo).save(u);
        // zapis raportu — dokładnie raz
        verify(reportRepo).save(r);
    }

    @Test
    void blockUser_wrongType_throwsIllegalState() {
        Report r = new Report();
        r.setType(ReportType.PRODUCT);
        when(reportRepo.findById(1L)).thenReturn(Optional.of(r));
        assertThrows(IllegalStateException.class, () -> service.blockUser(1L));
    }

    @Test
    void blockUser_missingReport_throwsIllegalArgument() {
        when(reportRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.blockUser(1L));
    }

    @Test
    void deleteProduct_success() {
        Report r = new Report();
        r.setType(ReportType.PRODUCT);
        when(reportRepo.findById(2L)).thenReturn(Optional.of(r));

        service.deleteProduct(2L);

        verify(productService).deleteProduct(r.getTargetId());
        assertThat(r.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        verify(reportRepo).save(r);
    }

    @Test
    void deleteProduct_andBlockOwner_success() {
        // przygotuj raport i produkt z właścicielem
        Report r = new Report();
        r.setType(ReportType.PRODUCT);
        r.setTargetId(88L);
        when(reportRepo.findById(3L)).thenReturn(Optional.of(r));

        Product p = new Product();
        User owner = new User();
        p.setOwner(owner);
        when(productRepo.findById(88L)).thenReturn(Optional.of(p));

        service.deleteProductAndBlockOwner(3L);

        assertThat(owner.isEnabled()).isFalse();
        verify(productService).deleteProduct(88L);
        assertThat(r.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        verify(userRepo).save(owner);
        verify(reportRepo).save(r);
    }

    @Test
    void deleteProductAndBlockOwner_wrongType_throws() {
        Report r = new Report();
        r.setType(ReportType.USER);
        when(reportRepo.findById(4L)).thenReturn(Optional.of(r));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.deleteProductAndBlockOwner(4L));
        assertThat(ex.getMessage()).contains("To nie jest zgłoszenie produktu");
    }
}
