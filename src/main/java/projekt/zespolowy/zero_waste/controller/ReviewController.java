package projekt.zespolowy.zero_waste.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.security.CustomUser;
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
    public String addReview(@ModelAttribute Review review, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        review.setUser(user);
        review.setCreatedDate(LocalDateTime.now());
        review.setTargetUserId((long) 7); // Ustaw ID użytkownika, którego dotyczy recenzja
        reviewService.createReview(review);

        return "redirect:/reviews";
    }

    @GetMapping
    public String getReviewsByUserId(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new Review());

        return "reviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Review review = reviewService.getReviewById(id);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Nie masz uprawnień do edycji tej recenzji.");
        }

        model.addAttribute("reviewToEdit", review);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewService.getReviewsByUserId(user.getId()));

        return "reviews";
    }

    @PostMapping("/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute Review updatedReview, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Review review = reviewService.getReviewById(id);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Nie masz uprawnień do edycji tej recenzji.");
        }

        review.setContent(updatedReview.getContent());
        review.setRating(updatedReview.getRating());
        reviewService.updateReview(review);

        double newAverageRating = reviewService.calculateAverageRating(user);
        user.setAverageRating(newAverageRating);
        userService.save(user);

        return "redirect:/reviews";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Review review = reviewService.getReviewById(id);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Nie masz uprawnień do usunięcia tej recenzji.");
        }

        reviewService.deleteReview(review);
        double newAverageRating = reviewService.calculateAverageRating(user);
        user.setAverageRating(newAverageRating);
        userService.save(user);

        return "redirect:/reviews";
    }
}
