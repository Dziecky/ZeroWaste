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

        // Ustaw datę utworzenia
        review.setCreatedDate(LocalDateTime.now());

        // Obsługa oceny (rating)
        if (review.getRating() < 0 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        // Zapisz recenzję
        reviewService.createReview(review);

        // Oblicz nową średnią ocenę użytkownika
        double newAverageRating = reviewService.calculateAverageRating(user);
        user.setAverageRating(newAverageRating);
        userService.save(user);
        return "redirect:/accountDetails"; // Powrót do strony szczegółów konta
    }


    //    @GetMapping
//    public String getAllReviews(Model model) {
//        List<ReviewDto> reviews = reviewService.getAllReviews(); // Pobranie wszystkich recenzji
//
//        model.addAttribute("reviews", reviews);
//        model.addAttribute("review", new Review()); // Pusty obiekt dla formularza dodawania recenzji
//        return "reviews"; // widok Thymeleaf do wyświetlania wszystkich recenzji i formularzy
//    }
    @GetMapping
    public String getReviewsByUserId(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new Review());
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
        return "redirect:/accountDetails";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Review review = reviewService.getReviewById(id);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Nie masz uprawnień do usunięcia tej recenzji.");
        }

        reviewService.deleteReview(review);
        // Oblicz nową średnią ocenę użytkownika
        double newAverageRating = reviewService.calculateAverageRating(user);
        user.setAverageRating(newAverageRating);
        userService.save(user);
        return "redirect:/accountDetails";
    }

}