package projekt.zespolowy.zero_waste.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.entity.enums.VoteType;
import projekt.zespolowy.zero_waste.mapper.ReviewMapper;
import projekt.zespolowy.zero_waste.repository.*;
import projekt.zespolowy.zero_waste.security.CustomUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService{

    private final ReviewRepository reviewRepository;
    private final UserTaskRepository userTaskRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Override
    @Transactional
    public Review createReview(Review review) {
        Review newReview = new Review();
        // Kopiuj pola z przekazanego obiektu review do newReview
        newReview.setContent(review.getContent());
        newReview.setCreatedDate(review.getCreatedDate());
        newReview.setRating(review.getRating());
        newReview.setTargetUserId(review.getTargetUserId());
        newReview.setUser(review.getUser());

        Task createReviewTask = taskRepository.findByTaskName("Dodaj pierwszą recenzję na profilu użytkownika");

        if (createReviewTask != null) {
            // Pobierz zadanie użytkownika
            UserTask userTask = userTaskRepository.findByUserAndTask(review.getUser(), createReviewTask);
            // Zwiększ postęp zadania
            userTask.setProgress(userTask.getProgress() + 1);

            // Sprawdź, czy zadanie zostało ukończone
            if (userTask.getProgress() >= createReviewTask.getRequiredActions()) {
                userTask.setCompleted(true);
                userTask.setCompletionDate(LocalDate.now());

                review.getUser().setTotalPoints(review.getUser().getTotalPoints() + createReviewTask.getPointsAwarded());
                userRepository.save(review.getUser()); // Zapisz zmiany w użytkowniku
            }

            userTaskRepository.save(userTask);
        }

        return reviewRepository.save(newReview);
    }

    @Override
    @Transactional
    public Review createResponse(Review review) {
        Review newReview = new Review();
        // Kopiuj pola z przekazanego obiektu review do newReview
        newReview.setContent(review.getContent());
        newReview.setCreatedDate(review.getCreatedDate());
        newReview.setRating(0);
        newReview.setTargetUserId(review.getTargetUserId());
        newReview.setUser(review.getUser());
        newReview.setParentReview(review.getParentReview());

        return reviewRepository.save(newReview);
    }


    @Override
    public Review updateReview(Review review) {
        Review existingReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getContent() != null) {
            existingReview.setContent(review.getContent());
        }
        existingReview.setRating(review.getRating());

        return reviewRepository.save(existingReview);
    }

    @Override
    public void deleteReview(Review review) {
        // Find all child reviews
        List<Review> childReviews = reviewRepository.findByParentReview(review);

        // Delete child reviews first
        for (Review child : childReviews) {
            reviewRepository.delete(child);
        }

        // Now delete the parent review
        reviewRepository.delete(review);
    }

//    public double calculateAverageRating(User user) {
//        List<ReviewDto> reviews = getReviewsByTargetUserId(user.getId());
//        if (reviews.isEmpty()) {
//            System.out.println("AverageRating dla: "+ user.getId() + " - PUSTA");
//            return 0.0;
//        }
//
//
//        double totalRating = reviews.stream()
//                .mapToInt(ReviewDto::getRating)
//                .sum();
//        System.out.println("AverageRating dla: "+ user.getId() + " - " + totalRating / reviews.size());
//        return totalRating / reviews.size();
//    }

    public double calculateAverageRating(User user) {
        List<ReviewDto> reviews = getReviewsByTargetUserId(user.getId());
        if (reviews.isEmpty()) {
            System.out.println("AverageRating dla: " + user.getId() + " - PUSTA");
            return 0.0;
        }

        // Filter out reviews with a rating of 0
        List<ReviewDto> filteredReviews = reviews.stream()
                .filter(review -> review.getRating() != 0)
                .collect(Collectors.toList());

        if (filteredReviews.isEmpty()) {
            System.out.println("AverageRating dla: " + user.getId() + " - All ratings are 0");
            return 0.0;
        }

        double totalRating = filteredReviews.stream()
                .mapToInt(ReviewDto::getRating)
                .sum();

        double averageRating = totalRating / filteredReviews.size();
        System.out.println("AverageRating dla: " + user.getId() + " - " + averageRating);
        return averageRating;
    }

    public List<ReviewDto> getReviewsByTargetUserId(Long targetUserId) {
        List<Review> reviews = reviewRepository.findByTargetUserId(targetUserId);

        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = customUser.getUser();

        return reviews.stream()
                .map(review -> {
                    String userVote = voteRepository.findByUserAndReview(currentUser, review)
                            .map(v -> v.getVoteType().toString())
                            .orElse(null);
                    return ReviewMapper.mapToReviewDto(review, userVote);
                })
                .collect(Collectors.toList());
    }


    public List<ReviewDto> getReviewsWithZeroRatingByTargetUserId(Long targetUserId) {
        List<Review> reviews = reviewRepository.findByTargetUserId(targetUserId);
        return reviews.stream()
                .filter(review -> review.getRating() == 0)
                .map(review -> ReviewMapper.mapToReviewDto(review, null))
                .collect(Collectors.toList());
    }



    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUserOrderByCreatedDateDesc(user);
    }

    public List<ReviewDto> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);

        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = customUser.getUser();

        return reviews.stream()
                .map(review -> {
                    String userVote = voteRepository.findByUserAndReview(currentUser, review)
                            .map(v -> v.getVoteType().toString())
                            .orElse(null);
                    return ReviewMapper.mapToReviewDto(review, userVote);
                })
                .collect(Collectors.toList());
    }


    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }


    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = customUser.getUser();

        return reviews.stream()
                .map(review -> {
                    String userVote = voteRepository.findByUserAndReview(currentUser, review)
                            .map(v -> v.getVoteType().toString())
                            .orElse(null);
                    return ReviewMapper.mapToReviewDto(review, userVote);
                })
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByTargetUserIdAndRating(Long targetUserId, int rating) {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = customUser.getUser();

        return reviewRepository.findByTargetUserIdAndRating(targetUserId, rating)
                .stream()
                .map(review -> {
                    String userVote = voteRepository.findByUserAndReview(currentUser, review)
                            .map(v -> v.getVoteType().toString())
                            .orElse(null);
                    return ReviewMapper.mapToReviewDto(review, userVote);
                })
                .collect(Collectors.toList());
    }

    private ReviewDto convertToDto(Review review) {
        return ReviewMapper.mapToReviewDto(review, null); // lub usuń metodę jeśli zbędna
    }


    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    public void vote(Long reviewId, Long userId, boolean isUpvote) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Sprawdź czy użytkownik już głosował
        Optional<Vote> existingVote = voteRepository.findByUserAndReview(user, review);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            // Jeśli głos jest taki sam jak poprzedni - anuluj
            if ((isUpvote && vote.getVoteType() == VoteType.UP) ||
                    (!isUpvote && vote.getVoteType() == VoteType.DOWN)) {
                // Usuń głos
                voteRepository.delete(vote);
                review.setVotes(review.getVotes() - (isUpvote ? 1 : -1));
            } else {
                // Zmień głos
                vote.setVoteType(isUpvote ? VoteType.UP : VoteType.DOWN);
                voteRepository.save(vote);
                review.setVotes(review.getVotes() + (isUpvote ? 2 : -2));
            }
        } else {
            // Nowy głos
            Vote vote = new Vote();
            vote.setUser(user);
            vote.setReview(review);
            vote.setVoteType(isUpvote ? VoteType.UP : VoteType.DOWN);
            voteRepository.save(vote);

            review.setVotes((review.getVotes() == null ? 0 : review.getVotes()) + (isUpvote ? 1 : -1));
        }

        reviewRepository.save(review);
    }

}
