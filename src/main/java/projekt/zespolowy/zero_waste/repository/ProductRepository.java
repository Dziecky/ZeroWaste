package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findAllByAvailableTrueAndAuctionFalse();
    List<Product>findByAuctionFalseOrderByCreatedAtDesc();
    Page<Product> findByAuctionFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Product> findByProductCategoryAndAuctionFalse(ProductCategory productCategory, Pageable pageable);
    Page<Product> findAllByOrderByCreatedAtAsc(Pageable pageable);
    Page<Product> findByProductCategoryAndAuctionFalseOrderByPriceAsc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findByProductCategoryAndAuctionFalseOrderByPriceDesc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findByAuctionFalseOrderByPriceAsc(Pageable pageable);
    Page<Product> findByAuctionFalseOrderByPriceDesc(Pageable pageable);
    Page<Product> findByProductCategoryAndAuctionFalseOrderByCreatedAtDesc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndAuctionFalseOrderByPriceAsc(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndAuctionFalseOrderByPriceDesc(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndAuctionFalseOrderByCreatedAtDesc(String name, Pageable pageable);

    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByPriceAsc(ProductCategory category, String name, Pageable pageable);
    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByPriceDesc(ProductCategory category, String name, Pageable pageable);
    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByCreatedAtDesc(ProductCategory category, String name, Pageable pageable);

    List<Product> findByAuctionTrueAndAvailableTrue();

    List<Product> findByAuctionFalseAndAvailableTrue();

    List<Product> findByOwnerIdAndAvailableFalse(Long ownerId);

    int countByOwnerId(Long userId);

    int countByOwnerIdAndAuction(Long userId, boolean auction);
}
