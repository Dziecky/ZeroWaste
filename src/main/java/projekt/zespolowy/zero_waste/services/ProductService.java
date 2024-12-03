package projekt.zespolowy.zero_waste.services;

import projekt.zespolowy.zero_waste.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Product saveProduct(Product product);

    Optional<Product> getProductById(Long id);

    void deleteProduct(Long id);

    public List<Product> getProductsByIds(List<Long> ids);
}
