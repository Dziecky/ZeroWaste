package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.Review;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long user_id;
    private Long target_id;
    private String content;
    private LocalDateTime createdDate;
    private String username;
    @Getter
    private String createdDateFormatted;
    private int rating;
    private Long parentReviewId;

    public ReviewDto(Long id,
                     Long target_id,
                     Long user_id,
                     String content,
                     LocalDateTime createdDate,
                     Integer rating,
                     String username,
                     String createdDateFormatted,
                     Review parentReview
    ) {
        this.id = id;
        this.user_id = user_id;
        this.target_id = target_id;
        this.content = content;
        this.createdDate = createdDate;
        this.rating = rating;
        this.username = username;
        this.createdDateFormatted = createdDateFormatted;
        this.parentReviewId = parentReview != null ? parentReview.getId() : null;
    }

    public Object getUserId() {
        return user_id;
    }

    public Long getTargetUserId() {
        return target_id;
    }
}
