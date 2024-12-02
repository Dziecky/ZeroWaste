package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;

    // Konstruktorowe wstrzykiwanie zależności
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accountDetails")
    public String accountDetails(Model model) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();
        model.addAttribute("user", user);
        return "accountDetails";
    }

    @GetMapping("/user/tasks")
    public String showUserTasks(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        List<UserTask> userTasks = userService.getUserTasksForUser(user);

        List<UserTask> completedTasks = userTasks.stream()
                .filter(UserTask::isCompleted)
                .collect(Collectors.toList());

        List<UserTask> incompleteTasks = userTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());

        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("incompleteTasks", incompleteTasks);

        return "userTasks";
    }
}
