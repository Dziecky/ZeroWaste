package projekt.zespolowy.zero_waste.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import projekt.zespolowy.zero_waste.dto.ProductDTO;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.EcoImpactService;
import projekt.zespolowy.zero_waste.repository.EcoImpactHistoryRep;
import projekt.zespolowy.zero_waste.services.MonthlyReportService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projekt.zespolowy.zero_waste.services.StatisticsService;
import projekt.zespolowy.zero_waste.services.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

@Controller
@RequiredArgsConstructor
public class EcoImpactController {

    private final EcoImpactService ecoImpactService;
    private final EcoImpactHistoryRep ecoImpactHistoryRep;
    private final UserService userService;
    private final MonthlyReportService monthlyReportService;
    private final StatisticsService statisticsService;
    private final ProductService productService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }

    @GetMapping("/eco-impact")
    public String getEcoImpact(Model model) {
        Long userId = getCurrentUserId();
        String ecoImpactMessage = ecoImpactService.calculateEcoImpact(userId);
        List<EcoImpactHistory> history = ecoImpactService.getEcoImpactHistory(userId);

        boolean hasImpactData = !history.isEmpty();


        List<Order> purchases = statisticsService.getPurchasesByUser(userId);
        List<Product> sales = statisticsService.getSalesByUser(userId);

        List<ProductDTO> purchaseDTOs = purchases.stream()
                .map(order -> new ProductDTO(
                        order.getProduct().getId(),
                        order.getProduct().getName(),
                        order.getProduct().getDescription(),
                        order.getProduct().getImageUrl(),
                        order.getProduct().isAvailable(),
                        order.getProduct().getPrice(),
                        order.getProduct().getCreatedAt(),
                        order.getProduct().getProductCategory(),
                        order.getProduct().getQuantity(),
                        order.getProduct().getUnitOfMeasure(),
                        order.getProduct().isAuction(),
                        order.getProduct().getEndDate(),
                        order.getProduct().getTags().stream()
                                .map(tag -> tag.getName())
                                .collect(Collectors.toSet())
                )).collect(Collectors.toList());

        List<ProductDTO> salesDTOs = sales.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getImageUrl(),
                        product.isAvailable(),
                        product.getPrice(),
                        product.getCreatedAt(),
                        product.getProductCategory(),
                        product.getQuantity(),
                        product.getUnitOfMeasure(),
                        product.isAuction(),
                        product.getEndDate(),
                        product.getTags().stream()
                                .map(tag -> tag.getName())
                                .collect(Collectors.toSet())
                )).collect(Collectors.toList());


        User user = userService.findById(userId);


        double waterSaved = history.stream().mapToDouble(EcoImpactHistory::getWaterSaved).sum();
        double co2Saved = history.stream().mapToDouble(EcoImpactHistory::getCo2Saved).sum();
        double energySaved = history.stream().mapToDouble(EcoImpactHistory::getEnergySaved).sum();
        double wasteReduced = history.stream().mapToDouble(EcoImpactHistory::getWasteReduced).sum();


        DecimalFormat df = new DecimalFormat("0.00");
        String formattedWaterSaved = df.format(waterSaved);
        String formattedCo2Saved = df.format(co2Saved);
        String formattedEnergySaved = df.format(energySaved);
        String formattedWasteReduced = df.format(wasteReduced);

        List<String> historyDates = new ArrayList<>();
        List<String> waterSavedHistory = new ArrayList<>();
        List<String> co2SavedHistory = new ArrayList<>();
        List<String> energySavedHistory = new ArrayList<>();
        List<String> wasteReducedHistory = new ArrayList<>();

        for (EcoImpactHistory record : history) {
            historyDates.add(record.getDate().toString());
            waterSavedHistory.add(df.format(record.getWaterSaved()));
            co2SavedHistory.add(df.format(record.getCo2Saved()));
            energySavedHistory.add(df.format(record.getEnergySaved()));
            wasteReducedHistory.add(df.format(record.getWasteReduced()));
        }


        model.addAttribute("ecoImpactMessage", ecoImpactMessage);
        model.addAttribute("waterSaved", formattedWaterSaved);
        model.addAttribute("co2Saved", formattedCo2Saved);
        model.addAttribute("energySaved", formattedEnergySaved);
        model.addAttribute("wasteReduced", formattedWasteReduced);
        model.addAttribute("historyDates", historyDates);
        model.addAttribute("waterSavedHistory", waterSavedHistory);
        model.addAttribute("co2SavedHistory", co2SavedHistory);
        model.addAttribute("energySavedHistory", energySavedHistory);
        model.addAttribute("wasteReducedHistory", wasteReducedHistory);

        model.addAttribute("points", user.getTotalPoints());
        model.addAttribute("ecoImpactMessage", ecoImpactMessage);

        model.addAttribute("purchases", purchaseDTOs);
        model.addAttribute("sales", salesDTOs);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        return "eco-impact";
    }

    @PostMapping("/generate-report")
    public String generateMonthlyReports(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            monthlyReportService.generateAndSendMonthlyReports(currentUser);
            model.addAttribute("reportGenerated", true);
        }

        return "redirect:/eco-impact";
    }
}
