package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.dto.FoodDTO;
import projekt.zespolowy.zero_waste.services.FoodService;

import java.util.List;

@Controller
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService productService;

    @GetMapping
    public String showForm() {
        return "Food/searchForm";
    }

// pojedyncze wyszukanie
//
//    @PostMapping("/search")
//    public String search(@RequestParam("query") String query, Model model) {
//        FoodDTO product = productService.searchProduct(query);
//        model.addAttribute("product", product);
//        return "Food/productResult";
//    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<FoodDTO> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        return "Food/productResult";
    }
}
