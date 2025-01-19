package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserTaskRepository userTaskRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.productRepository = productRepository;
        this.userTaskRepository = userTaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    public Product saveProduct(Product product) {
        // Zapisz produkt
        Product savedProduct = productRepository.save(product);

        // Sprawdź, czy użytkownik ma zadanie "Dodaj produkt"
        Task addProductTask = taskRepository.findByTaskName("Dodaj pierwszy przedmiot");

        if (addProductTask != null) {
            // Pobierz zadanie użytkownika
            UserTask userTask = userTaskRepository.findByUserAndTask(product.getOwner(), addProductTask);

            if (userTask != null && !userTask.isCompleted()) {
                // Zwiększ postęp zadania
                userTask.setProgress(userTask.getProgress() + 1);

                // Sprawdź, czy zadanie zostało ukończone
                if (userTask.getProgress() >= addProductTask.getRequiredActions()) {
                    userTask.setCompleted(true);
                    userTask.setCompletionDate(LocalDate.now());

                    // Dodaj punkty za zadanie do użytkownika
                    User user = product.getOwner();
                    user.setTotalPoints(user.getTotalPoints() + addProductTask.getPointsAwarded());
                    userRepository.save(user); // Zapisz zmiany w użytkowniku
                }

                // Zapisz zmiany w UserTask
                userTaskRepository.save(userTask);
            }
        }

        return savedProduct;
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);

    }
    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    public Page<Product> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByProductCategory(category, pageable);
    }


    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceAsc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAllByOrderByPriceAsc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseOrderByPriceAsc(search.trim(), pageable);
    }

    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceDesc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAllByOrderByPriceDesc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseOrderByPriceDesc(search.trim(), pageable);
    }

    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByDateDesc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(search.trim(), pageable);
    }

    // Metody dla wyszukiwania z kategorią
    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceAsc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryOrderByPriceAsc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseOrderByPriceAsc(category, search.trim(), pageable);
    }

    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceDesc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryOrderByPriceDesc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseOrderByPriceDesc(category, search.trim(), pageable);
    }

    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByDateDesc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryOrderByCreatedAtDesc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseOrderByCreatedAtDesc(category, search.trim(), pageable);
    }
}
