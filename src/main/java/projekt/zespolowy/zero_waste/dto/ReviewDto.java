package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.enums.VoteType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long targetUserId;
    private String content;
    private LocalDateTime createdDate;
    private String username;
    private String createdDateFormatted;
    private int rating;
    private Long parentReviewId;
    private Integer votes = 0;  // Wartość domyślna 0
    private VoteType userVote;  // Typ głosu aktualnego użytkownika (UP, DOWN lub null)

    public ReviewDto(Long id,
                     Long targetUserId,
                     Long userId,
                     String content,
                     LocalDateTime createdDate,
                     Integer rating,
                     String username,
                     String createdDateFormatted,
                     Review parentReview,
                     Integer votes) {
        this.id = id;
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.content = content;
        this.createdDate = createdDate;
        this.rating = rating;
        this.username = username;
        this.createdDateFormatted = createdDateFormatted;
        this.parentReviewId = parentReview != null ? parentReview.getId() : null;
        this.votes = votes != null ? votes : 0;
    }

    // Gettery i settery są generowane przez Lombok

    // Metoda pomocnicza do sprawdzania czy użytkownik dał upvote
    public boolean isUpvoted() {
        return VoteType.UP.equals(userVote);
    }

    // Metoda pomocnicza do sprawdzania czy użytkownik dał downvote
    public boolean isDownvoted() {
        return VoteType.DOWN.equals(userVote);
    }

    public void setUserVote(String s) {
        if (s == null) {
            this.userVote = null; // Brak głosu
        } else if (s.equals("UPVOTE")) {
            this.userVote = VoteType.UP;
        } else if (s.equals("DOWNVOTE")) {
            this.userVote = VoteType.DOWN;
        }
    }
}