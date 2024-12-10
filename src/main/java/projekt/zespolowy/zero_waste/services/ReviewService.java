package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.mapper.ReviewMapper;
import projekt.zespolowy.zero_waste.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService{

    private final ReviewRepository reviewRepository;
    private final UserService userService;

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
        reviewRepository.delete(review);
    }


    public double calculateAverageRating(User user) {
        List<ReviewDto> reviews = getReviewsByTargetUserId(user.getId());
        if (reviews.isEmpty()) {
            System.out.println("AverageRating dla: "+ user.getId() + " - PUSTA");
            return 0.0;
        }


        double totalRating = reviews.stream()
                .mapToInt(ReviewDto::getRating)
                .sum();
        System.out.println("AverageRating dla: "+ user.getId() + " - " + totalRating / reviews.size());
        return totalRating / reviews.size();
    }
    public List<ReviewDto> getReviewsByTargetUserId(Long targetUserId) {
        List<Review> reviews = reviewRepository.findByTargetUserId(targetUserId);
        return reviews.stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUserOrderByCreatedDateDesc(user);
    }

    public List<ReviewDto> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            reviewDtos.add(ReviewMapper.mapToReviewDto(review));
        }
        return reviewDtos;
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }


    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            reviewDtos.add(ReviewMapper.mapToReviewDto(review));
        }
        return reviewDtos;
    }
    public List<ReviewDto> getReviewsByTargetUserIdAndRating(Long targetUserId, int rating) {
        return reviewRepository.findByTargetUserIdAndRating(targetUserId, rating)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private ReviewDto convertToDto(Review review) {
        return ReviewMapper.mapToReviewDto(review);
    }

    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }
}
