package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.Report;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.ReportStatus;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.repository.ReportRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;

    @Autowired
    public ReportService(ReportRepository reportRepo,
                         UserRepository userRepo,
                         ProductRepository productRepo,
                         ProductService productService) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.productService = productService;
    }

    public void createReport(ReportType type, Long targetId, User reporter, String reason) {
        Report r = new Report();
        r.setType(type);
        r.setTargetId(targetId);
        r.setReporter(reporter);
        r.setReason(reason);
        reportRepo.save(r);
    }

    public Page<Report> getNewReports(int page, int size) {
        return reportRepo.findByStatus(ReportStatus.NEW, PageRequest.of(page, size));
    }

    public Page<Report> getResolvedReports(int page, int size) {
        return reportRepo.findByStatus(ReportStatus.RESOLVED, PageRequest.of(page, size));
    }

    @Transactional
    public void blockUser(Long reportId) {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.USER) throw new IllegalStateException();
        User u = userRepo.findById(r.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("Brak użytkownika"));
        u.setEnabled(false);
        userRepo.save(u);
        r.setStatus(ReportStatus.RESOLVED);
        reportRepo.save(r);
    }

    @Transactional
    public void deleteProduct(Long reportId) {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.PRODUCT) throw new IllegalStateException();
        productService.deleteProduct(r.getTargetId());
        r.setStatus(ReportStatus.RESOLVED);
        reportRepo.save(r);
    }

    @Transactional
    public void deleteProductAndBlockOwner(Long reportId) {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.PRODUCT) throw new IllegalStateException("To nie jest zgłoszenie produktu");

        // pobierz produkt przed usunięciem, aby znać właściciela
        Product p = productRepo.findById(r.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("Brak produktu"));
        User owner = p.getOwner(); // albo p.getUser(), w zależności od relacji w encji

        // blokujemy użytkownika
        owner.setEnabled(false);
        userRepo.save(owner);

        // usuwamy produkt
        productService.deleteProduct(r.getTargetId());

        // oznaczamy zgłoszenie jako przetworzone
        r.setStatus(ReportStatus.RESOLVED);
        reportRepo.save(r);
    }

}

