package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.Report;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.entity.enums.ReportStatus;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.repository.ReportRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;
    private final ActivityLogService logService;

    @Autowired
    public ReportService(ReportRepository reportRepo,
                         UserRepository userRepo,
                         ProductRepository productRepo,
                         ProductService productService, ActivityLogService logService) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.productService = productService;
        this.logService = logService;
    }

    public void createReport(ReportType type, Long targetId, User reporter, String reason) {
        Report r = new Report();
        r.setType(type);
        r.setTargetId(targetId);
        r.setReporter(reporter);
        r.setReason(reason);
        reportRepo.save(r);
        logService.log(UserService.getUser().getId(), ActivityType.ITEM_REMOVED, r.getId(), Map.of(
                "type", type,
                "targetId", targetId,
                "reason", reason
        ));
    }

    public Page<Report> getNewReports(int page, int size) {
        return reportRepo.findByStatus(ReportStatus.NEW, PageRequest.of(page, size));
    }

    public Page<Report> getResolvedReports(int page, int size) {
        return reportRepo.findByStatus(ReportStatus.RESOLVED, PageRequest.of(page, size));
    }

    @Transactional
    public void blockUser(Long reportId) {
        // Znajdź główne zgłoszenie i weryfikuj typ
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.USER) throw new IllegalStateException();

        // Zablokuj użytkownika
        User u = userRepo.findById(r.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("Brak użytkownika"));
        u.setEnabled(false);
        userRepo.save(u);

        // Zaktualizuj wszystkie zgłoszenia o tym samym typie i targetId
        List<Report> all = reportRepo.findByTypeAndTargetId(ReportType.USER, r.getTargetId());
        for (Report rep : all) {
            rep.setStatus(ReportStatus.RESOLVED);
        }
        reportRepo.saveAll(all);

        // Logowanie akcji (z użyciem oryginalnego ID wywołania)
        logService.log(UserService.getUser().getId(), ActivityType.USER_BLOCKED, u.getId(), Map.of(
                "reportId", reportId,
                "Blocked User", u.getUsername()
        ));
    }

    @Transactional
    public void deleteProduct(Long reportId) {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.PRODUCT) throw new IllegalStateException();

        // Usuń produkt, jeśli istnieje
        productRepo.findById(r.getTargetId()).ifPresent(p ->
                productService.deleteProduct(p.getId())
        );

        // Zaktualizuj wszystkie zgłoszenia o tym samym typie i targetId
        List<Report> all = reportRepo.findByTypeAndTargetId(ReportType.PRODUCT, r.getTargetId());
        for (Report rep : all) {
            rep.setStatus(ReportStatus.RESOLVED);
        }
        reportRepo.saveAll(all);
    }

    @Transactional
    public void deleteProductAndBlockOwner(Long reportId) {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Brak zgłoszenia"));
        if (r.getType() != ReportType.PRODUCT)
            throw new IllegalStateException("To nie jest zgłoszenie produktu");

        // Jeśli produkt istnieje, pobierz właściciela i zablokuj go
        productRepo.findById(r.getTargetId()).ifPresent(p -> {
            User owner = p.getOwner();
            owner.setEnabled(false);
            userRepo.save(owner);
            logService.log(UserService.getUser().getId(), ActivityType.USER_BLOCKED, owner.getId(), Map.of(
                    "reportId", reportId,
                    "Blocked User", owner.getUsername()
            ));

            // Usuń produkt
            productService.deleteProduct(p.getId());
        });

        // Zaktualizuj wszystkie zgłoszenia o tym samym typie i targetId
        List<Report> all = reportRepo.findByTypeAndTargetId(ReportType.PRODUCT, r.getTargetId());
        for (Report rep : all) {
            rep.setStatus(ReportStatus.RESOLVED);
        }
        reportRepo.saveAll(all);
    }

}

