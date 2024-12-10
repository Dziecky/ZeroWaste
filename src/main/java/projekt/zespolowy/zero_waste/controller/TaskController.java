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

    @GetMapping("/newTask")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "Tasks/newTask";
    }

    @PostMapping("/newTask")
    public String createTask(@ModelAttribute Task task) {
        taskService.createAndAssignNewTask(task);
        return "redirect:/tasks/showAllTasks";
    }

    @GetMapping("/showAllTasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "Tasks/showAllTasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks/showAllTasks";
    }
}
