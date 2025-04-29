package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductPriceHistory;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ProductPriceHistoryRepository  extends JpaRepository<ProductPriceHistory, Long> {

    List<ProductPriceHistory> findByProductIdAndCreatedAtAfter(Long productId, LocalDateTime date);

    ProductPriceHistory findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(Long productId, LocalDateTime date);
}
