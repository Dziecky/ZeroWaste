package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
