package projekt.zespolowy.zero_waste.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductService {
 List<Product> getAllProducts();
 Page<Product> getAllProducts(Pageable pageable);

 Product saveProduct(Product product);

 Optional<Product> getProductById(Long id);

 void deleteProduct(Long id);
 List<Product> getAvailableProducts();
 List<Product> getProductsByIds(List<Long> ids);
 Page<Product> getProductsByCategory(ProductCategory category, Pageable pageable);

 Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceAsc(String search, Pageable pageable);
 Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceDesc(String search, Pageable pageable);
 Page<Product> getProductsByNameContainingIgnoreCaseSortedByDateDesc(String search, Pageable pageable);

 Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceAsc(ProductCategory category, String search, Pageable pageable);
 Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceDesc(ProductCategory category, String search, Pageable pageable);
 Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByDateDesc(ProductCategory category, String search, Pageable pageable);

 Page<Product> getProductsNotOnAuction(Pageable pageable);
 List<Product> getAuctionProducts();

 void addToViewHistory(HttpSession session, Long productId);
 List<Product> getRecentlyViewedProducts(HttpSession session);
 List<Product> getRecentlyViewedProductsExcept(HttpSession session, Long excludeProductId);
}
