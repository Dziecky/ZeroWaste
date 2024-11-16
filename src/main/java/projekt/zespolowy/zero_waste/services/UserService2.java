package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;

import java.util.List;

@Service
public class UserService2 {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Transactional
    public void assignExistingTasksToNewUser(User newUser) { // przypadek, kiedy tworzymy uzytkownika i chcemy dodac mu istniejace taski
        // Zapisz nowego użytkownika w bazie danych
        userRepository.save(newUser);

        // Pobierz wszystkie istniejące zadania
        List<Task> allTasks = taskRepository.findAll();

        // Dla każdego zadania przypisz je do nowego użytkownika
        for (Task task : allTasks) {
            UserTask userTask = new UserTask();
            userTask.setUser(newUser);
            userTask.setTask(task);
            userTask.setProgress(0);  // Możesz ustawić domyślny postęp na 0
            userTask.setCompleted(false);  // Zadanie na początku nie jest ukończone
            userTask.setCompletionDate(null);  // Brak daty ukończenia, dopóki zadanie nie zostanie ukończone

            // Zapisz obiekt UserTask w bazie
            userTaskRepository.save(userTask);
        }
    }
}
