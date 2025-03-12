package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.http.HttpSession;
import projekt.zespolowy.zero_waste.entity.Product;
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



}
