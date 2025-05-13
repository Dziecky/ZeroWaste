package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.Data;
import projekt.zespolowy.zero_waste.config.JsonAttributeConverter;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@Table(name = "activity_log", indexes = {
        @Index(name = "idx_user_time", columnList = "user_id, occurred_at"),
})
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", length = 50, nullable = false)
    private ActivityType activityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonAttributeConverter.class)
    private Map<String, Object> details;

    public ActivityLog() {
    }

    public ActivityLog(Long userId, LocalDateTime occurredAt, ActivityType activityType, Long entityId, Map<String, Object> details) {
        this.userId = userId;
        this.occurredAt = occurredAt;
        this.activityType = activityType;
        this.entityId = entityId;
        this.details = details;
    }
}
