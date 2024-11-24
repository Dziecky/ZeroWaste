package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.TaskType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
//    // Znajdź zadania o określonym typie
//    List<Task> findByTaskType(TaskType taskType);
//
//    // Znajdź wszystkie zadania o określonym zakresie dat
//    List<Task> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
//
//    // Znajdź zadania o punktach większych niż
//    List<Task> findByPointsAwardedGreaterThan(int points);

    List<Task> findAll();
}
