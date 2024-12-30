package projekt.zespolowy.zero_waste.mapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class ReviewMapper {
    private final UserService userService;

    public static ReviewDto mapToReviewDto(Review review) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatowanie daty
        String formattedDate = review.getCreatedDate().format(formatter);

        return new ReviewDto(
                review.getId(),
                review.getTargetUserId(),
                review.getUser().getId(),
                review.getContent(),
                review.getCreatedDate(),
                review.getRating(),
                review.getUser().getUsername(),
                formattedDate,
                review.getParentReview()
        );
    }

}
