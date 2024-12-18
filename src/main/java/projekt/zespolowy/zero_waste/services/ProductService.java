package projekt.zespolowy.zero_waste.services;

import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Product saveProduct(Product product);

    Optional<Product> getProductById(Long id);

    void deleteProduct(Long id);

    List<Product> getProductsByIds(List<Long> ids);
    List<Product> getProductsByCategory(ProductCategory category);

    List<Product> getAllProductsSortedByPriceAsc();

    List<Product> getAllProductsSortedByPriceDesc();

    List<Product> getProductsByCategorySortedByPriceAsc(ProductCategory category);

    List<Product> getProductsByCategorySortedByPriceDesc(ProductCategory category);
    List<Product> getAllProductsSortedByDateAsc();

    List<Product> getAllProductsSortedByDateDesc();


}
