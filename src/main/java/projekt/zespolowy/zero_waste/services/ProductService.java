package projekt.zespolowy.zero_waste.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductService {

   List<Product>getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);

    Product saveProduct(Product product);

    Optional<Product> getProductById(Long id);

    void deleteProduct(Long id);

    List<Product> getProductsByIds(List<Long> ids);
    Page<Product> getProductsByCategory(ProductCategory category, Pageable pageable);

    Page<Product> getAllProductsSortedByPriceAsc(Pageable pageable);
    Page<Product> getAllProductsSortedByPriceDesc(Pageable pageable);

    Page<Product> getProductsByCategorySortedByPriceAsc(ProductCategory category, Pageable pageable);
    Page<Product> getProductsByCategorySortedByPriceDesc(ProductCategory category, Pageable pageable);
    Page<Product> getAllProductsSortedByDateDesc(Pageable pageable);
    Page<Product> getProductsByCategorySortedByDateDesc(ProductCategory category, Pageable pageable);

}
