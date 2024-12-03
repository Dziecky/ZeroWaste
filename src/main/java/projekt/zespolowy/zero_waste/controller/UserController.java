package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    private final ReviewService reviewService;

    // Konstruktorowe wstrzykiwanie zależności
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/accountDetails")
    public String accountDetails(Model model) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        // Pobierz recenzje użytkownika
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getId());

        // Przekaż dane do widoku
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new Review()); // Obiekt dla formularza

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
