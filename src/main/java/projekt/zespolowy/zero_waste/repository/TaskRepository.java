package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.TaskType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Znajdź zadania o określonym typie
    List<Task> findByTaskType(TaskType taskType);

    @Query("SELECT t FROM Task t WHERE t.task_name = :taskName")
    Task findByTaskName(@Param("taskName") String taskName);

    List<Task> findAll();
}
