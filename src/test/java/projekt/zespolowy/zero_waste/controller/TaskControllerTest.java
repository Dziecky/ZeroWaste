package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.TaskType;
import projekt.zespolowy.zero_waste.services.TaskService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        task = new Task();
        task.setId(1L);
        task.setTask_name("Test Task");
        task.setTaskDescription("Test Description");
        task.setPointsAwarded(10);
        task.setTaskType(TaskType.BASIC);
    }

    @Test
    void testCreateTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/newTask"))
                .andExpect(status().isOk())
                .andExpect(view().name("Tasks/newTask"));
    }

    @Test
    void testCreateTask() throws Exception {
        // Given
        Task mockTask = new Task();
        mockTask.setTask_name("Test Task");
        mockTask.setTaskDescription("Test Description");
        mockTask.setPointsAwarded(10);
        mockTask.setTaskType(TaskType.BASIC); // Przypisz wartość z enum

        // Stubujemy metodę z odpowiednim typem enum
        doNothing().when(taskService).createAndAssignNewTask(any(Task.class));

        // When
        mockMvc.perform(post("/tasks/newTask")
                        .param("taskName", mockTask.getTask_name())
                        .param("taskDescription", mockTask.getTaskDescription())
                        .param("pointsAwarded", String.valueOf(mockTask.getPointsAwarded()))
                        .param("taskType", mockTask.getTaskType().name())) // Przekazujemy nazwę enum
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/showAllTasks"));

        // Then
        verify(taskService, times(1)).createAndAssignNewTask(any(Task.class)); // Sprawdzamy wywołanie metody z dowolnym obiektem Task
    }

    @Test
    void testDeleteTask() throws Exception {
        Long taskId = 1L;

        // When
        mockMvc.perform(get("/tasks/delete/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/showAllTasks"));

        // Then
        verify(taskService, times(1)).deleteTask(taskId);
    }
}