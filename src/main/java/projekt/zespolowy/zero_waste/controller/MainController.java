package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.ReportType;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.ReportService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
public class MainController {

    private final ProductService productService;
    @Autowired
    private ReportService reportService;

    public MainController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Product> availableProducts = productService.getAvailableProducts();
        model.addAttribute("products", availableProducts);
        return "index";
    }

    @PostMapping("/reports/create")
    public String createReport(@RequestParam ReportType type,
                               @RequestParam Long targetId,
                               @RequestParam String reason) {
        User reporter = UserService.getUser();
        reportService.createReport(type, targetId, reporter, reason);
        if (type == ReportType.PRODUCT)
            return "redirect:/products/view/" + targetId + "?reported";
        return "redirect:/user/" + targetId + "?reported";
    }

}

