package projekt.zespolowy.zero_waste.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.EcoImpactService;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequiredArgsConstructor
public class EcoImpactController {

    private final EcoImpactService ecoImpactService;

    @Autowired
    private final UserService userService;

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

        double waterSaved = history.stream().mapToDouble(EcoImpactHistory::getWaterSaved).sum();
        double co2Saved = history.stream().mapToDouble(EcoImpactHistory::getCo2Saved).sum();
        double energySaved = history.stream().mapToDouble(EcoImpactHistory::getEnergySaved).sum();
        double wasteReduced = history.stream().mapToDouble(EcoImpactHistory::getWasteReduced).sum();


        List<String> historyDates = new ArrayList<>();
        List<Double> waterSavedHistory = new ArrayList<>();
        List<Double> co2SavedHistory = new ArrayList<>();
        List<Double> energySavedHistory = new ArrayList<>();
        List<Double> wasteReducedHistory = new ArrayList<>();

        for (EcoImpactHistory record : history) {
            historyDates.add(record.getDate().toString());
            waterSavedHistory.add(record.getWaterSaved());
            co2SavedHistory.add(record.getCo2Saved());
            energySavedHistory.add(record.getEnergySaved());
            wasteReducedHistory.add(record.getWasteReduced());
        }

        model.addAttribute("ecoImpactMessage", ecoImpactMessage);
        model.addAttribute("waterSaved", waterSaved);
        model.addAttribute("co2Saved", co2Saved);
        model.addAttribute("energySaved", energySaved);
        model.addAttribute("wasteReduced", wasteReduced);
        model.addAttribute("historyDates", historyDates);
        model.addAttribute("waterSavedHistory", waterSavedHistory);
        model.addAttribute("co2SavedHistory", co2SavedHistory);
        model.addAttribute("energySavedHistory", energySavedHistory);
        model.addAttribute("wasteReducedHistory", wasteReducedHistory);

        return "eco-impact";
    }
}
