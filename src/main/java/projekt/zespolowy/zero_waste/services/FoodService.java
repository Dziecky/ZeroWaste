package projekt.zespolowy.zero_waste.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import projekt.zespolowy.zero_waste.dto.FoodDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodService {

    public FoodDTO searchProduct(String query) {
        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&search_simple=1&action=process&json=1";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);
        JSONArray products = json.getJSONArray("products");

        if (products.length() == 0) return null;

        JSONObject product = products.getJSONObject(0);
        FoodDTO dto = new FoodDTO();

        dto.setProductName(product.optString("product_name", "Brak"));
        dto.setBrand(product.optString("brands", "Brak"));
        dto.setNutriScore(product.optString("nutriscore_grade", "brak"));
        dto.setEcoScore(product.optString("ecoscore_grade", "brak"));
        dto.setIngredients(product.optString("ingredients_text", "Brak składu"));
        dto.setImageUrl(product.optString("image_url", ""));

        return dto;
    }

    public List<FoodDTO> searchProducts(String query) {
        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&search_simple=1&action=process&json=1";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);
        JSONArray products = json.getJSONArray("products");

        List<FoodDTO> results = new ArrayList<>();
        int limit = Math.min(products.length(), 9);

        for (int i = 0; i < limit; i++) {
            JSONObject product = products.getJSONObject(i);
            FoodDTO dto = new FoodDTO();

            dto.setProductName(product.optString("product_name", "Brak"));
            dto.setBrand(product.optString("brands", "Brak"));
            dto.setNutriScore(product.optString("nutriscore_grade", "brak"));
            dto.setEcoScore(product.optString("ecoscore_grade", "brak"));
            dto.setIngredients(product.optString("ingredients_text", "Brak składu"));
            dto.setImageUrl(product.optString("image_url", ""));

            results.add(dto);
        }

        return results;
    }
}
