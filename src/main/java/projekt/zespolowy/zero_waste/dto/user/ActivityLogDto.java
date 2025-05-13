package projekt.zespolowy.zero_waste.dto.user;

import lombok.Data;
import projekt.zespolowy.zero_waste.entity.ActivityLog;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityLogDto {
    private LocalDateTime occurredAt;
    private ActivityType activityType;
    private Map<String, Object> details;

    public static ActivityLogDto fromEntity(ActivityLog e) {
        ActivityLogDto dto = new ActivityLogDto();
        dto.setOccurredAt(e.getOccurredAt());
        dto.setActivityType(e.getActivityType());
        dto.setDetails(e.getDetails());
        return dto;
    }
}
