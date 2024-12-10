package projekt.zespolowy.zero_waste.services;

import jakarta.annotation.PostConstruct;
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
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Transactional
    public void createAndAssignNewTask(Task task) { // przypadek z nowymi zadaniami
        // Zapisz nowe zadanie w bazie danych
        taskRepository.save(task);

        // Pobierz wszystkich użytkowników
        List<User> allUsers = userRepository.findAll();

        // Dla każdego użytkownika przypisz nowe zadanie
        for (User user : allUsers) {
            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTask(task);
            userTask.setProgress(0);
            userTask.setCompleted(false);
            userTask.setCompletionDate(null);

            // Zapisz obiekt UserTask w bazie
            userTaskRepository.save(userTask);
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void deleteTask(Long id) {
        // Usuń wszystkie powiązania z tabeli user_task
        userTaskRepository.deleteByTaskId(id);

        // Usuń zadanie z tabeli task
        taskRepository.deleteById(id);
    }

    public List<UserTask> getAllTasksForUser(User user) {
        return userTaskRepository.findByUser(user);
    }
}