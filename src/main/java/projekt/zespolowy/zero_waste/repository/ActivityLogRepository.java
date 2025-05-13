package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.ActivityLog;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    Page<ActivityLog> findByUserIdAndActivityType(Long userId, ActivityType type, Pageable pageable);
}
