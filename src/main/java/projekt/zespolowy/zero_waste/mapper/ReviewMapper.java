package projekt.zespolowy.zero_waste.mapper;

import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import java.time.format.DateTimeFormatter;

public class ReviewMapper {

    public static ReviewDto mapToReviewDto(Review review) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatowanie daty
        String formattedDate = review.getCreatedDate().format(formatter);

        return new ReviewDto(
                review.getId(),
                review.getContent(),
                review.getCreatedDate(),
                review.getRating(),
                formattedDate // Przekazanie sformatowanej daty
        );
    }

}
