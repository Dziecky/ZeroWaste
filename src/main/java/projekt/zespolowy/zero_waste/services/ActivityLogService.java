package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.ActivityLog;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.repository.ActivityLogRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ActivityLogService {
    @Autowired
    private ActivityLogRepository logRepository;

    public void log(Long userId, ActivityType type, Long entityId, Map<String, Object> details) {
        ActivityLog entry = new ActivityLog(
                userId,
                LocalDateTime.now(),
                type,
                entityId,
                details
        );
        logRepository.save(entry);
    }
}
