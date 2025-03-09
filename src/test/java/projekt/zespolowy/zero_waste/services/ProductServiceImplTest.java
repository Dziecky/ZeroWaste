package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import projekt.zespolowy.zero_waste.repository.ProductRepository;
import projekt.zespolowy.zero_waste.services.ProductServiceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsCountByUserId() {
        // Given
        Long userId = 1L;
        int expectedCount = 5;
        when(productRepository.countByOwnerId(userId)).thenReturn(expectedCount); // Mockowanie repozytorium

        // When
        int actualCount = productService.getProductsCountByUserId(userId);

        // Then
        assertEquals(expectedCount, actualCount); // Sprawdzenie wyniku
        verify(productRepository, times(1)).countByOwnerId(userId); // Weryfikacja wywołania metody
    }

    @Test
    void testGetProductsCountByUserIdAndAuction() {
        // Given
        Long userId = 1L;
        boolean auction = true;
        int expectedCount = 3;
        when(productRepository.countByOwnerIdAndAuction(userId, auction)).thenReturn(expectedCount); // Mockowanie repozytorium

        // When
        int actualCount = productService.getProductsCountByUserIdAndAuction(userId, auction);

        // Then
        assertEquals(expectedCount, actualCount); // Sprawdzenie wyniku
        verify(productRepository, times(1)).countByOwnerIdAndAuction(userId, auction); // Weryfikacja wywołania metody
    }
}