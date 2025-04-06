package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import projekt.zespolowy.zero_waste.entity.PrivacySettings;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.ProductServiceImpl;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserPageControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ProductServiceImpl productServiceImpl;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CustomUser customUser;

    @InjectMocks
    private UserPageController userPageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void accountDetails_UserNotFound_ReturnsErrorPage() {
        // Arrange
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(null);

        // Act
        String viewName = userPageController.accountDetails(userId, model);

        // Assert
        assertEquals("redirect:/error", viewName);
    }

    @Test
    void addReview_ValidReview_RedirectsToUserPage() {
        // Arrange
        Long userId = 1L;
        Review review = new Review();
        User user = new User();
        user.setId(userId);

        when(authentication.getPrincipal()).thenReturn(customUser);
        when(customUser.getUser()).thenReturn(user);
        when(userService.findById(userId)).thenReturn(user);

        // Act
        String viewName = userPageController.addReview(userId, review);

        // Assert
        assertEquals("redirect:/user/" + userId, viewName);
        verify(reviewService).createReview(review);
        verify(userService).save(user);
    }

    @Test
    void deleteReview_ValidReview_RedirectsToUserPage() {
        // Arrange
        Long userId = 1L;
        Long reviewId = 1L;
        Review review = new Review();
        User user = new User();

        when(reviewService.findById(reviewId)).thenReturn(review);
        when(userService.findById(userId)).thenReturn(user);

        // Act
        String viewName = userPageController.deleteReview(userId, reviewId);

        // Assert
        assertEquals("redirect:/user/" + userId, viewName);
        verify(reviewService).deleteReview(review);
        verify(userService).save(user);
    }

    @Test
    void filterReviews_WithRating_FiltersReviews() {
        // Arrange
        Long userId = 1L;
        Integer rating = 5;
        User user = new User();

        when(userService.findById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(customUser);
        when(customUser.getUser()).thenReturn(user);

        // Act
        String viewName = userPageController.filterReviews(userId, rating, model);

        // Assert
        assertEquals("user", viewName);
        verify(reviewService).getReviewsByTargetUserIdAndRating(userId, rating);
        verify(model).addAttribute("selectedRating", rating);
    }
}