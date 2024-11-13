package projekt.zespolowy.zero_waste.services;


import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;

public interface IReviewService {
    Review createReview(Review review);
    Review updateReview(Review review);
    void deleteReview(Review review);
    double calculateAverageRating(User user);
    List<Review> getReviewsByUser(User user);

    Review getReviewById(Long id);
//    List<ReviewDto> getReviewsByUserId(Long userId);

}
