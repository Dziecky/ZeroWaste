package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.services.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Display the form for creating a new task
    @GetMapping("/newTask")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "newTask";
    }

    // Handle the creation of a new task and assign it to users
    @PostMapping("/newTask")
    public String createTask(@ModelAttribute Task task) {
        taskService.createAndAssignNewTask(task); // Call service to save the task and assign it to users
        return "redirect:/tasks/showAllTasks"; // Redirect to the task list after creating the task
    }

    // Display the list of all tasks
    @GetMapping("/showAllTasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks()); // Add all tasks to the model
        return "showAllTasks"; // Returns the task.html template to display the list of tasks
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks/showAllTasks";
    }
}
