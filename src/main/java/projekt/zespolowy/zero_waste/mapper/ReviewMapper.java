package projekt.zespolowy.zero_waste.mapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.VoteType;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.VoteRepository;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@AllArgsConstructor
public class ReviewMapper {
    private final UserService userService;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;


    public static ReviewDto mapToReviewDto(Review review, String userVote) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = review.getCreatedDate().format(formatter);

        ReviewDto reviewDto = new ReviewDto(
                review.getId(),
                review.getTargetUserId(),
                review.getUser().getId(),
                review.getContent(),
                review.getCreatedDate(),
                review.getRating(),
                review.getUser().getUsername(),
                formattedDate,
                review.getParentReview(),
                review.getVotes()
        );

        reviewDto.setUserVote(userVote);
        return reviewDto;
    }
}

