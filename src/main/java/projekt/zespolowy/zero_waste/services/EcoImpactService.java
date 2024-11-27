package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.EcoImpactHistoryRep;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;
import projekt.zespolowy.zero_waste.dto.EcoImpact;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EcoImpactService {

    private final UserRepository userRepository;
    private final EcoImpactHistoryRep ecoImpactHistoryRepository;

    public String calculateEcoImpact(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int totalPoints = user.getTotalPoints();

        double waterSaved = totalPoints * 0.1;
        double co2Saved = totalPoints * 0.05;
        double energySaved = totalPoints * 0.2;
        double wasteReduced = totalPoints * 0.3;


        EcoImpactHistory history = new EcoImpactHistory();
        history.setUser(user);
        history.setWaterSaved(waterSaved);
        history.setCo2Saved(co2Saved);
        history.setEnergySaved(energySaved);
        history.setWasteReduced(wasteReduced);
        history.setDate(LocalDate.now());
        ecoImpactHistoryRepository.save(history);

        return String.format("You have saved %.2f liters of water, %.2f kg of CO2, %.2f kWh of energy, and %.2f kg of waste.", waterSaved, co2Saved, energySaved, wasteReduced);
    }

    public List<EcoImpactHistory> getEcoImpactHistory(Long userId) {
        return ecoImpactHistoryRepository.findByUserId(userId);
    }
}
