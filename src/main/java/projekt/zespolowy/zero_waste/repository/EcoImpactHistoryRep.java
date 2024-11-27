package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;

import java.util.List;

public interface EcoImpactHistoryRep extends JpaRepository<EcoImpactHistory, Long> {
    List<EcoImpactHistory> findByUserId(Long userId);
}
