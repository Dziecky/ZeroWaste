package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "user_task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "task_progress", nullable = false)
    private int progress;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "task_completion_date")
    private LocalDate completionDate;
}

