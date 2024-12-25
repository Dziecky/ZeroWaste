package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EcoImpactHistoryRep extends JpaRepository<EcoImpactHistory, Long> {


    List<EcoImpactHistory> findByUserId(Long userId);

    Optional<EcoImpactHistory> findFirstByUserIdOrderByDateDesc(Long userId);

    List<EcoImpactHistory> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
