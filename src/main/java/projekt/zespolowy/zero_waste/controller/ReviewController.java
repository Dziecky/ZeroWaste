package projekt.zespolowy.zero_waste.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    public String createReview(@ModelAttribute Review review, Principal principal) {
        // Pobierz zalogowanego użytkownika
        User user = userService.findByUsername(principal.getName());

        // Powiąż recenzję z użytkownikiem
        review.setUser(user);
        review.setCreatedDate(LocalDateTime.now()); // Ustaw datę utworzenia

        reviewService.createReview(review);
        return "redirect:/accountDetails"; // Powrót do strony szczegółów konta
    }


    @GetMapping("/user")
    public String getReviewsByUserId(@RequestParam("userId") Long userId, Model model) {
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(userId);

        if (reviews.isEmpty()) {
            model.addAttribute("errorMessage", "No reviews found for user ID " + userId);
        } else {
            model.addAttribute("reviews", reviews);
        }

        return "user-reviews"; // widok Thymeleaf do wyświetlania recenzji użytkownika
    }

    @GetMapping("/{id}")
    public String getReview(@PathVariable Long id, Model model) {
        Review review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        return "review-details"; // widok Thymeleaf do wyświetlania szczegółów recenzji
    }

    @GetMapping
    public String getAllReviews(Model model) {
        List<ReviewDto> reviews = reviewService.getAllReviews(); // Pobranie wszystkich recenzji
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review()); // Pusty obiekt dla formularza dodawania recenzji
        return "reviews"; // widok Thymeleaf do wyświetlania wszystkich recenzji i formularzy
    }

}