package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Getter
    private String createdDateFormatted;
    private int rating;

    public ReviewDto(Long id, Long target_id, Long user_id, String content, LocalDateTime createdDate, Integer rating, String createdDateFormatted) {
        this.id = id;
        this.user_id = user_id;
        this.target_id = target_id;
        this.content = content;
        this.createdDate = createdDate;
        this.rating = rating;
        this.createdDateFormatted = createdDateFormatted;
    }

    public Object getUserId() {
        return user_id;
    }
}
