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
    private String content;
    private LocalDateTime createdDate;
    @Getter
    private String createdDateFormatted;
    private int rating;

    public ReviewDto(Long id, String content, LocalDateTime createdDate, Integer rating, String createdDateFormatted) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.rating = rating;
        this.createdDateFormatted = createdDateFormatted;
    }

    public void setCreatedDateFormatted(String createdDateFormatted) {
        this.createdDateFormatted = createdDateFormatted;
    }

}
