package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;

import java.util.List;
import java.util.Optional;

public interface EcoImpactHistoryRep extends JpaRepository<EcoImpactHistory, Long> {

    // Метод для получения истории по userId
    List<EcoImpactHistory> findByUserId(Long userId);

    // Метод для получения последнего вклада пользователя по дате (сортировка по дате)
    Optional<EcoImpactHistory> findFirstByUserIdOrderByDateDesc(Long userId);
}
