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
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.FavoriteProductRepository;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.services.ProductServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FavoriteProductRepository favoriteProductRepository;

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


}
