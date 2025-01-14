package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product>findAllByOrderByCreatedAtDesc();
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Product> findByProductCategory(ProductCategory productCategory, Pageable pageable);
    Page<Product> findAllByOrderByCreatedAtAsc(Pageable pageable);
    Page<Product> findByProductCategoryOrderByPriceAsc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findByProductCategoryOrderByPriceDesc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);
    Page<Product> findAllByOrderByPriceDesc(Pageable pageable);
    Page<Product> findByProductCategoryOrderByCreatedAtDesc(ProductCategory productCategory, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseOrderByPriceAsc(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseOrderByPriceDesc(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name, Pageable pageable);

    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseOrderByPriceAsc(ProductCategory category, String name, Pageable pageable);
    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseOrderByPriceDesc(ProductCategory category, String name, Pageable pageable);
    Page<Product> findByProductCategoryAndNameContainingIgnoreCaseOrderByCreatedAtDesc(ProductCategory category, String name, Pageable pageable);
}
