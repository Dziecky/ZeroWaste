package projekt.zespolowy.zero_waste.services;


import org.springframework.data.domain.Page;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;

public interface IReviewService {
    Review createReview(Review review);
    Review createResponse(Review review);
    Review updateReview(Review review);
    void deleteReview(Review review);
    double calculateAverageRating(User user);
    List<Review> getReviewsByUser(User user);

    Review getReviewById(Long id);
//    List<ReviewDto> getReviewsByUserId(Long userId);
    Page<ReviewDto> getReviewsByTargetUserIdPaginated(Long userId, int page, int size);

    Page<ReviewDto> getReviewsByTargetUserIdAndRatingPaginated(Long userId, Integer rating, int page, int size);
}
