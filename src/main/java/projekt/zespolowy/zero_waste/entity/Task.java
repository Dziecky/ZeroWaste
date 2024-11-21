package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String task_name;

    @Column(name = "task_description", nullable = false)
    private String taskDescription;

    @Column(name = "required_actions", nullable = false)
    private int requiredActions; // l. wymaganych akcji aby zakonczyc taska

    @Column(name = "points_awarded", nullable = false)
    private int pointsAwarded; // przyznawane za wykonanie

    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @Column(name = "task_start_date")
    private LocalDate startDate; // tylko dla zadan okresowych

    @Column(name = "task_end_date")
    private LocalDate endDate; // tylko dla zadan okresowych
}

