package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserPageController {
    private final UserService userService;
    private final ReviewService reviewService;

    // Konstruktorowe wstrzykiwanie zależności
    public UserPageController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public String accountDetails(@PathVariable("id") Long userId, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/error";
        }

        List<ReviewDto> reviewsAboutUser = reviewService.getReviewsByTargetUserId(userId);

        // Pobierz ID zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long currentUserId = customUser.getUser().getId();

        // Dodaj informację o tym, które recenzje należą do zalogowanego użytkownika
        Map<Long, Boolean> reviewOwnership = reviewsAboutUser.stream()
                .collect(Collectors.toMap(ReviewDto::getId, r -> r.getUserId().equals(currentUserId)));

        model.addAttribute("user", user);
        model.addAttribute("reviewsAboutUser", reviewsAboutUser);
        model.addAttribute("averageRating", user.getAverageRating());
        model.addAttribute("newReview", new Review());
        model.addAttribute("reviewOwnership", reviewOwnership);

        return "user";
    }

    @GetMapping("/{id}/filter-reviews")
    public String filterReviews(@PathVariable Long id, @RequestParam(required = false) Integer rating, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/error";
        }

        List<ReviewDto> reviewsAboutUser;
        if (rating != null && rating >= 1 && rating <= 5) {
            reviewsAboutUser = reviewService.getReviewsByTargetUserIdAndRating(id, rating);
        } else {
            reviewsAboutUser = reviewService.getReviewsByTargetUserId(id);
        }

        model.addAttribute("user", user);
        model.addAttribute("reviewsAboutUser", reviewsAboutUser);
        model.addAttribute("averageRating", user.getAverageRating());
        model.addAttribute("selectedRating", rating);
        model.addAttribute("newReview", new Review());

        return "user";
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable Long id, @ModelAttribute Review review) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User currentUser = customUser.getUser();

        review.setUser(currentUser);
        review.setTargetUserId(id);
        review.setCreatedDate(LocalDateTime.now());

        reviewService.createReview(review);

        User user = userService.findById(id);
        user.setAverageRating(reviewService.calculateAverageRating(user));

        userService.save(user);

        return "redirect:/user/" + id;
    }
    @PostMapping("/{id}/reviews/{reviewId}/edit")
    public String editReview(@PathVariable Long id, @PathVariable Long reviewId, @ModelAttribute Review review) {
        Review existingReview = reviewService.findById(reviewId);
        if (existingReview == null) {
            return "redirect:/error";
        }

        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        reviewService.updateReview(existingReview);

        User user = userService.findById(id);
        user.setAverageRating(reviewService.calculateAverageRating(user));

        return "redirect:/user/" + id;
    }

    @PostMapping("/{id}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long id, @PathVariable Long reviewId) {
        Review existingReview = reviewService.findById(reviewId);
        if (existingReview == null) {
            return "redirect:/error";
        }

        reviewService.deleteReview(existingReview);

        User user = userService.findById(id);
        user.setAverageRating(reviewService.calculateAverageRating(user));
        userService.save(user);

        return "redirect:/user/" + id;
    }
    @GetMapping("/{id}/reviews/{reviewId}/edit")
    public String showEditReviewForm(@PathVariable Long id, @PathVariable Long reviewId, Model model) {
        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return "redirect:/error";
        }

        model.addAttribute("reviewToEdit", review);
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("reviewsAboutUser", reviewService.getReviewsByTargetUserId(id));
        model.addAttribute("newReview", new Review()); // Add this line

        return "user";
    }

}

