package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface EcoImpactHistoryRep extends JpaRepository<EcoImpactHistory, Long> {
    @Query("SELECT e FROM EcoImpactHistory e WHERE e.user.id = :userId ORDER BY e.date DESC")
    Page<EcoImpactHistory> findByUserIdOrderByDateDesc(@Param("userId") Long userId, Pageable pageable);
    List<EcoImpactHistory> findByUserId(Long userId);
    List<EcoImpactHistory> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
