package projekt.zespolowy.zero_waste.services;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private final FavoriteProductRepository favoriteProductRepository;

    private ProductPriceHistoryRepository productPriceHistoryRepository;

    static final String VIEWED_PRODUCTS_SESSION_KEY = "viewedProducts";
    private static final int MAX_HISTORY_SIZE = 6;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserTaskRepository userTaskRepository, UserRepository userRepository, TaskRepository taskRepository, FavoriteProductRepository favoriteProductRepository, ProductPriceHistoryRepository productPriceHistoryRepository) {
        this.productRepository = productRepository;
        this.userTaskRepository = userTaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.favoriteProductRepository = favoriteProductRepository;
        this.productPriceHistoryRepository = productPriceHistoryRepository;
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
        ProductPriceHistory priceHistory = new ProductPriceHistory(savedProduct, product.getPrice(), LocalDateTime.now());
        productPriceHistoryRepository.save(priceHistory);
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

    @Transactional
    public Product handleProductAfterPurchase(String productName) {
        Optional<Product> maybeProduct = productRepository.findByName(productName);

        if(maybeProduct.isPresent()) {
            Product product = maybeProduct.get();
            product.setAvailable(false);
            return productRepository.save(product);
        }
        return null;
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
    public List<Product> getAvailableProducts() {
        return productRepository.findAllByAvailableTrueAndAuctionFalse();
    }

    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    public Page<Product> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByProductCategoryAndAuctionFalse(category, pageable);
    }


    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceAsc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByAuctionFalseOrderByPriceAsc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseAndAuctionFalseOrderByPriceAsc(search.trim(), pageable);
    }

    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByPriceDesc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByAuctionFalseOrderByPriceDesc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseAndAuctionFalseOrderByPriceDesc(search.trim(), pageable);
    }

    public Page<Product> getProductsByNameContainingIgnoreCaseSortedByDateDesc(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByAuctionFalseOrderByCreatedAtDesc(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseAndAuctionFalseOrderByCreatedAtDesc(search.trim(), pageable);
    }

    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceAsc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryAndAuctionFalseOrderByPriceAsc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByPriceAsc(category, search.trim(), pageable);
    }

    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceDesc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryAndAuctionFalseOrderByPriceDesc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByPriceDesc(category, search.trim(), pageable);
    }

    public Page<Product> getProductsByCategoryAndNameContainingIgnoreCaseSortedByDateDesc(ProductCategory category, String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findByProductCategoryAndAuctionFalseOrderByCreatedAtDesc(category, pageable);
        }
        return productRepository.findByProductCategoryAndNameContainingIgnoreCaseAndAuctionFalseOrderByCreatedAtDesc(category, search.trim(), pageable);
    }

    public Page<Product> getProductsNotOnAuction(Pageable pageable) {
        return productRepository.findByAuctionFalseOrderByCreatedAtDesc(pageable);
    }

    public List<Product> getAuctionProducts() {
        return productRepository.findByAuctionTrueAndAvailableTrue();
    }
    public List<Product> getProductsNotOnAuction() {
        return productRepository.findByAuctionFalseAndAvailableTrue();
    }
    public int getProductsCountByUserId(Long userId) {
        return productRepository.countByOwnerId(userId);
    }
    public int getProductsCountByUserIdAndAuction(Long userId, boolean auction) {
        return productRepository.countByOwnerIdAndAuction(userId, auction);
    }

    @Override
    public void addToViewHistory(HttpSession session, Long productId) {
        LinkedList<Long> viewedProducts = (LinkedList<Long>) session.getAttribute(VIEWED_PRODUCTS_SESSION_KEY);
        if (viewedProducts == null) {
            viewedProducts = new LinkedList<>();
        }
        viewedProducts.remove(productId);
        viewedProducts.addFirst(productId);
        while (viewedProducts.size() > MAX_HISTORY_SIZE) {
            viewedProducts.removeLast();
        }

        session.setAttribute(VIEWED_PRODUCTS_SESSION_KEY, viewedProducts);
    }

    @Override
    public List<Product> getRecentlyViewedProducts(HttpSession session) {
        LinkedList<Long> viewedProductIds = (LinkedList<Long>) session.getAttribute(VIEWED_PRODUCTS_SESSION_KEY);
        List<Product> recentlyViewedProducts = new ArrayList<>();

        if (viewedProductIds != null && !viewedProductIds.isEmpty()) {
            for (Long id : viewedProductIds) {
                getProductById(id).ifPresent(recentlyViewedProducts::add);
            }
        }

        return recentlyViewedProducts;
    }

    @Override
    public List<Product> getRecentlyViewedProductsExcept(HttpSession session, Long excludeProductId) {
        List<Product> viewHistory = getRecentlyViewedProducts(session);

        return viewHistory.stream()
                .filter(product -> !product.getId().equals(excludeProductId))
                .collect(Collectors.toList());
    }

    @Override
    public void addFavoriteProduct(Long userId, Long productId) {
        boolean alreadyFavorite = favoriteProductRepository
                .findByUserIdAndProductId(userId, productId).isPresent();
        if (!alreadyFavorite) {
            FavoriteProduct favorite = new FavoriteProduct();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
            User user = new User();
            user.setId(userId);
            favorite.setUser(user);
            favorite.setProduct(product);
            favoriteProductRepository.save(favorite);
        }
    }

    @Override
    public void removeFavoriteProduct(Long userId, Long productId) {
        Optional<FavoriteProduct> favorite = favoriteProductRepository.findByUserIdAndProductId(userId, productId);
        favorite.ifPresent(favoriteProductRepository::delete);
    }



    @Override
    public boolean isProductFavorite(Long userId, Long productId) {
        return favoriteProductRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }


    @Override
    public List<Product> getFavoriteProducts(Long userId) {
        List<FavoriteProduct> favorites = favoriteProductRepository.findByUserId(userId);
        return favorites.stream()
                .map(FavoriteProduct::getProduct)
                .toList();
    }

    public void incrementViewCount(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        Product product = getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID: " + productId));
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }
    @Override
    public Double getLowestPriceInLast30Days(Long productId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        ProductPriceHistory lowestPriceHistory = productPriceHistoryRepository
                .findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(productId, thirtyDaysAgo);

        if (lowestPriceHistory == null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product ID cannot be null"));
            return product.getPrice();
        }

        return lowestPriceHistory.getPrice();
    }




}
