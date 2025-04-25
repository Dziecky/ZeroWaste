package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import projekt.zespolowy.zero_waste.dto.FoodDTO;
import projekt.zespolowy.zero_waste.services.FoodService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodControllerTest {

    private FoodService foodService;
    private FoodController foodController;
    private Model model;

    @BeforeEach
    void setUp() {
        foodService = mock(FoodService.class);
        foodController = new FoodController();

        // ręcznie wstrzykujemy mocka (bo nie używamy Springa w teście)
        foodController.productService = foodService;

        model = mock(Model.class);
    }

    @Test
    void testSearch_ReturnsProductResultViewWithModelAttribute() {
        // given
        String query = "Nutella";
        FoodDTO product = new FoodDTO();
        product.setProductName("Nutella");
        product.setBrand("Ferrero");

        List<FoodDTO> productList = List.of(product);

        when(foodService.searchProducts(query)).thenReturn(productList);

        // when
        String viewName = foodController.search(query, model);

        // then
        assertEquals("Food/productResult", viewName);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("products"), captor.capture());

        List capturedProducts = captor.getValue();
        assertEquals(1, capturedProducts.size());
        assertEquals("Nutella", ((FoodDTO) capturedProducts.get(0)).getProductName());

        verify(foodService).searchProducts(query);
    }
}
