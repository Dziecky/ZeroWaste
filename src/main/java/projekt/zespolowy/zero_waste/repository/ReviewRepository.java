package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser(User user);
    List<Review> findByUserOrderByCreatedDateDesc(User user);
    List<Review> findByUserId(Long userId);
    List<Review> findByTargetUserId(Long targetUserId);
    List<Review> findByTargetUserIdAndRating(Long targetUserId, int rating);
}
