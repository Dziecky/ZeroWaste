package projekt.zespolowy.zero_waste.controller;
import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import projekt.zespolowy.zero_waste.dto.OrderStatsDTO;
import projekt.zespolowy.zero_waste.dto.ProductDTO;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.EcoImpactService;
import projekt.zespolowy.zero_waste.repository.EcoImpactHistoryRep;
import projekt.zespolowy.zero_waste.services.MonthlyReportService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projekt.zespolowy.zero_waste.services.StatisticsService;
import projekt.zespolowy.zero_waste.services.ProductService;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import projekt.zespolowy.zero_waste.services.PdfReportService;


class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
@Controller
@RequiredArgsConstructor
public class EcoImpactController {

    private final TemplateEngine templateEngine;
    private final EcoImpactService ecoImpactService;
    private final EcoImpactHistoryRep ecoImpactHistoryRep;
    private final UserService userService;
    private final MonthlyReportService monthlyReportService;
    private final StatisticsService statisticsService;
    private final ProductService productService;
    private final PdfReportService pdfReportService;
    private static final Logger logger = LoggerFactory.getLogger(EcoImpactController.class);


    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }

    @GetMapping("/eco-impact")

    public String getEcoImpact(@RequestParam(defaultValue = "0") int page, Model model) {
        Long userId = getCurrentUserId();
        String ecoImpactMessage = ecoImpactService.calculateEcoImpact(userId);
        Page<EcoImpactHistory> historyPage = ecoImpactService.getEcoImpactHistoryPaginated(userId, page, 5);
        List<EcoImpactHistory> history = historyPage.getContent();
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("currentPage", page);


        List<String> historyDates = new ArrayList<>();
        List<Double> totalPointsHistory = new ArrayList<>();

        for (EcoImpactHistory record : history) {
            historyDates.add(record.getDate().toString());
            totalPointsHistory.add((double) record.getTotalPoints());
        }

        model.addAttribute("historyDates", historyDates);
        model.addAttribute("totalPointsHistory", totalPointsHistory);


        double waterSaved = history.stream().mapToDouble(EcoImpactHistory::getWaterSaved).sum();
        double co2Saved = history.stream().mapToDouble(EcoImpactHistory::getCo2Saved).sum();
        double energySaved = history.stream().mapToDouble(EcoImpactHistory::getEnergySaved).sum();
        double wasteReduced = history.stream().mapToDouble(EcoImpactHistory::getWasteReduced).sum();

        DecimalFormat df = new DecimalFormat("0.00");
        String formattedWaterSaved = df.format(waterSaved);
        String formattedCo2Saved = df.format(co2Saved);
        String formattedEnergySaved = df.format(energySaved);
        String formattedWasteReduced = df.format(wasteReduced);


        List<String> waterSavedHistory = new ArrayList<>();
        List<String> co2SavedHistory = new ArrayList<>();
        List<String> energySavedHistory = new ArrayList<>();
        List<String> wasteReducedHistory = new ArrayList<>();

        for (EcoImpactHistory record : history) {
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


        User user = userService.findById(userId);
        model.addAttribute("points", user.getTotalPoints());

        model.addAttribute("showTransactionsLink", true);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "eco-impact";
    }

    @GetMapping("/transaction-stats")
    public String getTransactionsStats(
            @RequestParam(required = false, defaultValue = "month") String period,
            Model model) {

        Long userId = getCurrentUserId();
        User user = userService.findById(userId);
        model.addAttribute("user", user);


        List<Order> purchases = statisticsService.getPurchasesByUser(userId);
        List<Product> sales = statisticsService.getSalesByUser(userId);

        if (user.getAccountType() == AccountType.BUSINESS) {
            List<OrderStatsDTO> categoryStats = statisticsService.getQuarterlyOrderStats();
            model.addAttribute("categoryStats", categoryStats);

            long totalCategoryOrders = categoryStats.stream()
                    .mapToLong(OrderStatsDTO::getOrderCount)
                    .sum();
            double totalCategoryAmount = categoryStats.stream()
                    .mapToDouble(OrderStatsDTO::getTotalAmount)
                    .sum();

            model.addAttribute("totalCategoryOrders", totalCategoryOrders);
            model.addAttribute("totalCategoryAmount", totalCategoryAmount);
        }
        List<ProductDTO> purchaseDTOs = purchases.stream()
                .map(order -> new ProductDTO(
                        order.getProduct().getId(),
                        order.getProduct().getName(),
                        order.getProduct().getDescription(),
                        order.getProduct().getImageUrl(),
                        order.getProduct().isAvailable(),
                        order.getProduct().getPrice(),
                        order.getCreatedAt(),
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


        int totalPurchases = purchases.stream()
                .mapToInt(p -> (int) p.getProduct().getQuantity())
                .sum();
        int totalSales = sales.stream()
                .mapToInt(p -> (int) p.getQuantity())
                .sum();


        model.addAttribute("purchases", purchaseDTOs);
        model.addAttribute("sales", salesDTOs);
        model.addAttribute("totalPurchases", totalPurchases);
        model.addAttribute("totalSales", totalSales);

        return "transaction-stats";
    }

    @GetMapping("/download-stats-pdf")
    public void downloadStatsPdf(HttpServletResponse response) {
        try {
            byte[] pdfBytes = pdfReportService.generateBusinessReport();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=business_report.pdf");
            response.getOutputStream().write(pdfBytes);
        } catch (IOException e) {
            logger.error("PDF generation failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/generate-report")
    public String generateMonthlyReports(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = userService.findByUsername("Administration");
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            monthlyReportService.generateAndSendMonthlyReports(sender);
            model.addAttribute("reportGenerated", true);
        }

        return "redirect:/eco-impact";
    }
}