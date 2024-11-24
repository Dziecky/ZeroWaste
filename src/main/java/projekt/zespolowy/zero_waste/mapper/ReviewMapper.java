package projekt.zespolowy.zero_waste.mapper;

import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;

public class ReviewMapper {

    public static ReviewDto mapToReviewDto(Review review) {
        return new ReviewDto(review.getId(), review.getContent(), review.getCreatedDate(), review.getRating());
    }
}
