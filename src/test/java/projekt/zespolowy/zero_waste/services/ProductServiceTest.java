package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.http.HttpSession;
import projekt.zespolowy.zero_waste.entity.FavoriteProduct;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductPriceHistory;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.FavoriteProductRepository;
import projekt.zespolowy.zero_waste.repository.ProductPriceHistoryRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.services.ProductServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FavoriteProductRepository favoriteProductRepository;
    @Mock
    private ProductPriceHistoryRepository productPriceHistoryRepository;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void testAddToViewHistory_whenSessionIsEmpty() {

        Long productId = 1L;

        productService.addToViewHistory(session, productId);

        LinkedList<Long> viewedProducts = (LinkedList<Long>) session.getAttribute(ProductServiceImpl.VIEWED_PRODUCTS_SESSION_KEY);
        assertNotNull(viewedProducts);
        assertEquals(1, viewedProducts.size());
        assertEquals(productId, viewedProducts.getFirst());
    }

    @Test
    void testAddToViewHistory_whenSessionHasProducts() {

        Long productId = 1L;
        session.setAttribute(ProductServiceImpl.VIEWED_PRODUCTS_SESSION_KEY, new LinkedList<>());


        productService.addToViewHistory(session, 2L);
        productService.addToViewHistory(session, productId);


        LinkedList<Long> viewedProducts = (LinkedList<Long>) session.getAttribute(ProductServiceImpl.VIEWED_PRODUCTS_SESSION_KEY);


        assertNotNull(viewedProducts);
        assertEquals(2, viewedProducts.size());
        assertEquals(productId, viewedProducts.getFirst());
        assertTrue(viewedProducts.contains(2L));
    }

    @Test
    void testGetRecentlyViewedProducts_whenHistoryIsEmpty() {
        session.setAttribute(ProductServiceImpl.VIEWED_PRODUCTS_SESSION_KEY, new LinkedList<>());

        List<Product> products = productService.getRecentlyViewedProducts(session);

        assertTrue(products.isEmpty());
    }

    @Test
    void testGetRecentlyViewedProducts_whenHistoryHasProducts() {
        LinkedList<Long> productIds = new LinkedList<>();
        productIds.add(1L);
        productIds.add(2L);
        session.setAttribute(ProductServiceImpl.VIEWED_PRODUCTS_SESSION_KEY, productIds);

        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        List<Product> products = productService.getRecentlyViewedProducts(session);

        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    void testAddFavoriteProduct_whenProductIsNotAlreadyFavorite() {
        Long userId = 1L;
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        User user = new User();
        user.setId(userId);

        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        productService.addFavoriteProduct(userId, productId);
        verify(favoriteProductRepository, times(1)).save(any(FavoriteProduct.class));
    }

    @Test
    void testRemoveFavoriteProduct_whenProductIsFavorite() {
        Long userId = 1L;
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        User user = new User();
        user.setId(userId);

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setUser(user);
        favoriteProduct.setProduct(product);
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(favoriteProduct));
        productService.removeFavoriteProduct(userId, productId);
        verify(favoriteProductRepository, times(1)).delete(favoriteProduct);
    }

    @Test
    void testRemoveFavoriteProduct_whenProductIsNotFavorite() {
        Long userId = 1L;
        Long productId = 1L;

        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        productService.removeFavoriteProduct(userId, productId);
        verify(favoriteProductRepository, never()).delete(any(FavoriteProduct.class));
    }

    @Test
    void testIsProductFavorite_whenProductIsFavorite() {
        Long userId = 1L;
        Long productId = 1L;
        FavoriteProduct favoriteProduct = new FavoriteProduct();
        Product product = new Product();
        product.setId(productId);
        favoriteProduct.setProduct(product);
        favoriteProduct.setUser(new User());
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(favoriteProduct));
        boolean isFavorite = productService.isProductFavorite(userId, productId);
        assertTrue(isFavorite);
    }

    @Test
    void testIsProductFavorite_whenProductIsNotFavorite() {
        Long userId = 1L;
        Long productId = 1L;

        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        boolean isFavorite = productService.isProductFavorite(userId, productId);
        assertFalse(isFavorite);
    }


    @Test
    void testGetFavoriteProducts_whenUserHasFavorites() {
        Long userId = 1L;
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        FavoriteProduct favorite1 = new FavoriteProduct();
        favorite1.setProduct(product1);
        FavoriteProduct favorite2 = new FavoriteProduct();
        favorite2.setProduct(product2);
        when(favoriteProductRepository.findByUserId(userId)).thenReturn(List.of(favorite1, favorite2));
        List<Product> favoriteProducts = productService.getFavoriteProducts(userId);
        assertEquals(2, favoriteProducts.size());
        assertTrue(favoriteProducts.contains(product1));
        assertTrue(favoriteProducts.contains(product2));
    }

    @Test
    void testGetFavoriteProducts_whenUserHasNoFavorites() {
        Long userId = 1L;

        when(favoriteProductRepository.findByUserId(userId)).thenReturn(List.of());
        List<Product> favoriteProducts = productService.getFavoriteProducts(userId);
        assertTrue(favoriteProducts.isEmpty());
    }

    @Test
    void testAddFavoriteProduct_whenUserIdIsNull() {
        Long userId = null;
        Long productId = 1L;
        assertThrows(IllegalArgumentException.class, () -> productService.addFavoriteProduct(userId, productId));
    }

    @Test
    void testAddFavoriteProduct_whenProductIdIsNull() {
        Long userId = 1L;
        Long productId = null;
        assertThrows(IllegalArgumentException.class, () -> productService.addFavoriteProduct(userId, productId));
    }

    @Test
    void testAddFavoriteProduct_whenProductNotFound() {
        Long userId = 1L;
        Long productId = 999L;

        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> productService.addFavoriteProduct(userId, productId));
    }

    @Test
    void testIncrementViewCount_Success() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setViewCount(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.incrementViewCount(productId);

        assertEquals(11, product.getViewCount());
        verify(productRepository, times(1)).save(product);
    }


    @Test
    void testIncrementViewCount_ProductNotFound() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            productService.incrementViewCount(productId);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testIncrementViewCount_MultipleIncrements() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setViewCount(0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        for (int i = 0; i < 5; i++) {
            productService.incrementViewCount(productId);
        }

        assertEquals(5, product.getViewCount());
        verify(productRepository, times(5)).save(product);
    }

    @Test
    void testIncrementViewCount_NullProductId() {
        Long productId = null;

        assertThrows(IllegalArgumentException.class, () -> {
            productService.incrementViewCount(productId);
        });

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testGetLowestPriceInLast30Days_whenPriceHistoryExists_thenReturnLowestPrice() {
        Long productId = 1L;
        ProductPriceHistory priceHistory1 = new ProductPriceHistory();
        priceHistory1.setPrice(10.0);
        priceHistory1.setCreatedAt(LocalDateTime.now().minusDays(10));

        ProductPriceHistory priceHistory2 = new ProductPriceHistory();
        priceHistory2.setPrice(5.0);
        priceHistory2.setCreatedAt(LocalDateTime.now().minusDays(5));

        ProductPriceHistory priceHistory3 = new ProductPriceHistory();
        priceHistory3.setPrice(8.0);
        priceHistory3.setCreatedAt(LocalDateTime.now().minusDays(2));

        List<ProductPriceHistory> priceHistories = List.of(priceHistory1, priceHistory2, priceHistory3);

        when(productPriceHistoryRepository.findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(eq(productId), any()))
                .thenReturn(priceHistory2);

        double result = productService.getLowestPriceInLast30Days(productId);

        assertEquals(5.0, result);
    }

    @Test
    void testGetLowestPriceInLast30Days_whenAllPricesAreTheSame_thenReturnThatPrice() {
        Long productId = 1L;
        ProductPriceHistory priceHistory1 = new ProductPriceHistory();
        priceHistory1.setPrice(7.0);
        priceHistory1.setCreatedAt(LocalDateTime.now().minusDays(10));

        ProductPriceHistory priceHistory2 = new ProductPriceHistory();
        priceHistory2.setPrice(7.0);
        priceHistory2.setCreatedAt(LocalDateTime.now().minusDays(5));

        ProductPriceHistory priceHistory3 = new ProductPriceHistory();
        priceHistory3.setPrice(7.0);
        priceHistory3.setCreatedAt(LocalDateTime.now().minusDays(2));

        when(productPriceHistoryRepository.findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(eq(productId), any()))
                .thenReturn(priceHistory1);

        double result = productService.getLowestPriceInLast30Days(productId);

        assertEquals(7.0, result, 0.001);
    }

    @Test
    void testGetLowestPriceInLast30Days_whenDifferentPrices_thenReturnLowestPrice() {
        Long productId = 1L;
        ProductPriceHistory priceHistory1 = new ProductPriceHistory();
        priceHistory1.setPrice(12.0);
        priceHistory1.setCreatedAt(LocalDateTime.now().minusDays(10));

        ProductPriceHistory priceHistory2 = new ProductPriceHistory();
        priceHistory2.setPrice(3.0);
        priceHistory2.setCreatedAt(LocalDateTime.now().minusDays(5));

        ProductPriceHistory priceHistory3 = new ProductPriceHistory();
        priceHistory3.setPrice(9.0);
        priceHistory3.setCreatedAt(LocalDateTime.now().minusDays(2));


        when(productPriceHistoryRepository.findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(eq(productId), any()))
                .thenReturn(priceHistory2);

        double result = productService.getLowestPriceInLast30Days(productId);

        assertEquals(3.0, result);
    }

    @Test
    void testGetLowestPriceInLast30Days_whenOlderPriceIsLower_thenItIsExcluded() {
        Long productId = 1L;

        ProductPriceHistory olderPriceHistory = new ProductPriceHistory();
        olderPriceHistory.setPrice(2.0);
        olderPriceHistory.setCreatedAt(LocalDateTime.now().minusDays(31));

        ProductPriceHistory priceHistory1 = new ProductPriceHistory();
        priceHistory1.setPrice(10.0);
        priceHistory1.setCreatedAt(LocalDateTime.now().minusDays(20));

        ProductPriceHistory priceHistory2 = new ProductPriceHistory();
        priceHistory2.setPrice(8.0);
        priceHistory2.setCreatedAt(LocalDateTime.now().minusDays(15));

        ProductPriceHistory priceHistory3 = new ProductPriceHistory();
        priceHistory3.setPrice(12.0);
        priceHistory3.setCreatedAt(LocalDateTime.now().minusDays(5));

        when(productPriceHistoryRepository.findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(eq(productId), any(LocalDateTime.class)))
                .thenReturn(priceHistory2);

        double result = productService.getLowestPriceInLast30Days(productId);

        assertEquals(8.0, result);

        verify(productPriceHistoryRepository).findTopByProductIdAndCreatedAtAfterOrderByPriceAsc(
                eq(productId), any(LocalDateTime.class));
    }
    @Test
    void testGetLowestPriceInLast30Days_whenProductIdIsNull_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> productService.getLowestPriceInLast30Days(null));
    }

    @Test
    void testGetEndedAuctions_returnsEndedAuctions() {
        LocalDateTime now = LocalDateTime.now();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Auction 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Auction 2");

        List<Product> endedAuctions = Arrays.asList(product1, product2);

        when(productRepository.findByAuctionTrueAndAvailableTrueAndEndDateBefore(now)).thenReturn(endedAuctions);

        List<Product> result = productService.getEndedAuctions(now);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Auction 1", result.get(0).getName());
        assertEquals("Auction 2", result.get(1).getName());

        verify(productRepository, times(1)).findByAuctionTrueAndAvailableTrueAndEndDateBefore(now);
    }


    @Test
    void testGetEndedAuctions_returnsEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        when(productRepository.findByAuctionTrueAndAvailableTrueAndEndDateBefore(now)).thenReturn(Collections.emptyList());

        List<Product> result = productService.getEndedAuctions(now);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).findByAuctionTrueAndAvailableTrueAndEndDateBefore(now);
    }
    @Test
    void testGetEndedAuctions_returnsNullFromRepository() {
        LocalDateTime now = LocalDateTime.now();
        when(productRepository.findByAuctionTrueAndAvailableTrueAndEndDateBefore(now)).thenReturn(null);

        List<Product> result = productService.getEndedAuctions(now);

        assertNull(result);
        verify(productRepository, times(1)).findByAuctionTrueAndAvailableTrueAndEndDateBefore(now);
    }




}
