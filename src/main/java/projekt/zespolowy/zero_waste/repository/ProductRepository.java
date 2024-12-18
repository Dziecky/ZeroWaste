package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findByProductCategory(ProductCategory productCategory);
    List<Product> findAllByOrderByCreatedAtAsc();
    List<Product> findByProductCategoryOrderByPriceAsc(ProductCategory productCategory);
    List<Product> findByProductCategoryOrderByPriceDesc(ProductCategory productCategory);
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();

}
