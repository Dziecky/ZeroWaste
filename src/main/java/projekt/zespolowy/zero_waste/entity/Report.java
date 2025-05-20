package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import projekt.zespolowy.zero_waste.entity.enums.ReportStatus;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;

import java.time.LocalDateTime;

@Data
@Entity
public class Report {
    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    private Long targetId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User reporter;

    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.NEW;
}
