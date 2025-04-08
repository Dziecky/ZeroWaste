package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static projekt.zespolowy.zero_waste.entity.TaskType.BASIC;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTask_name("New task");
        task.setTaskDescription("Test Description");
        task.setRequiredActions(2);
        task.setPointsAwarded(10);
        task.setTaskType(BASIC);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void testCreateAndAssignNewTask() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        // When
        taskService.createAndAssignNewTask(task);

        // Then
        verify(taskRepository, times(1)).save(task);
        verify(userTaskRepository, times(1)).save(any(UserTask.class));
    }

    @Test
    void testDeleteTask() {
        // Given
        Long taskId = 1L;

        // When
        taskService.deleteTask(taskId);

        // Then
        verify(userTaskRepository, times(1)).deleteByTaskId(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testGetAllTasksForUser() {
        // Given
        when(userTaskRepository.findByUser(user)).thenReturn(Arrays.asList(new UserTask()));

        // When
        List<UserTask> userTasks = taskService.getAllTasksForUser(user);

        // Then
        verify(userTaskRepository, times(1)).findByUser(user);
    }
}
