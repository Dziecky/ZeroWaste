package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Bid;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByProductOrderByAmountDesc(Product product);
    List<Bid> findByUserOrderByCreatedAtDesc(User user);
    Optional<Bid> findFirstByProductOrderByAmountDesc(Product product);
}

