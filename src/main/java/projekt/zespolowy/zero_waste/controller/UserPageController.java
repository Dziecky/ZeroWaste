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

        model.addAttribute("user", user);
        model.addAttribute("reviewsAboutUser", reviewsAboutUser);
        model.addAttribute("averageRating", user.getAverageRating());
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
}

