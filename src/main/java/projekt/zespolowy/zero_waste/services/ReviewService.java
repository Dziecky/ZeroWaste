package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.mapper.ReviewMapper;
import projekt.zespolowy.zero_waste.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService{

    private final ReviewRepository reviewRepository;

    @Override
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        Review existingReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new RuntimeException("Review not found"));
        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        // Możesz zaktualizować inne pola, jeśli są potrzebne
        return reviewRepository.save(existingReview);
    }

    @Override
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }


    public double calculateAverageRating(User user) {
        List<Review> reviews = reviewRepository.findByUser(user);
        if (reviews.isEmpty()) return 0.0;

        double totalRating = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        return totalRating / reviews.size();
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
}
