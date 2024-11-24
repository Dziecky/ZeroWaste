package projekt.zespolowy.zero_waste.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.services.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public String createReview(@ModelAttribute Review review, Model model) {
        review.setCreatedDate(LocalDateTime.now()); // Ustawienie bieżącej daty jako daty utworzenia
        Review createdReview = reviewService.createReview(review);
        model.addAttribute("review", createdReview);
        return "redirect:/reviews"; // przekierowanie do listy recenzji
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