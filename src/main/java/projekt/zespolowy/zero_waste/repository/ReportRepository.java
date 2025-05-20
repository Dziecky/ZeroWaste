package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Report;
import projekt.zespolowy.zero_waste.entity.enums.ReportStatus;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    List<Report> findByType(ReportType type);
    List<Report> findByTypeAndTargetId(ReportType reportType, Long targetId);
}
