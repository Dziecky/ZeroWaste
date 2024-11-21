package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
//
//    // Znajdź wszystkie zadania przypisane do użytkownika
//    List<UserTask> findByUser(User user);
//
//    // Znajdź wszystkie zadania przypisane do użytkownika i określonego zadania
//    Optional<UserTask> findByUserAndTask(User user, Task task);
//
//    // Znajdź wszystkie ukończone zadania dla użytkownika
//    List<UserTask> findByUserAndIsCompletedTrue(User user);
//
//    // Znajdź wszystkie nieukończone zadania dla użytkownika
//    List<UserTask> findByUserAndIsCompletedFalse(User user);
//
//    // Znajdź wszystkie zadania z określonym postępem dla danego użytkownika
//    List<UserTask> findByUserAndProgressGreaterThan(User user, int progress);

}

