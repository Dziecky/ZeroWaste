package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    private final ReviewService reviewService;

    // Constructor Injection
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/accountDetails")
    public String accountDetails(Model model, Principal principal) {
        // Pobierz aktualnie zalogowanego użytkownika
        User user = userService.findByUsername(principal.getName());

        // Pobierz recenzje użytkownika
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getId());

        // Przekaż dane do widoku
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new Review()); // Obiekt dla formularza

        return "accountDetails";
    }
}
