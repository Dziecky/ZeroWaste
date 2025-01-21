package projekt.zespolowy.zero_waste.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.EcoImpactHistoryRep;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.entity.EcoImpactHistory;

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


        List<EcoImpactHistory> history = ecoImpactHistoryRepository.findByUserId(userId);
        EcoImpactHistory lastHistoryRecord = history.isEmpty() ? null : history.get(history.size() - 1);


        if (lastHistoryRecord != null) {
            double lastWaterSaved = lastHistoryRecord.getWaterSaved();
            double lastCo2Saved = lastHistoryRecord.getCo2Saved();
            double lastEnergySaved = lastHistoryRecord.getEnergySaved();
            double lastWasteReduced = lastHistoryRecord.getWasteReduced();


            if (lastWaterSaved == totalPoints * 0.1 &&
                    lastCo2Saved == totalPoints * 0.05 &&
                    lastEnergySaved == totalPoints * 0.2 &&
                    lastWasteReduced == totalPoints * 0.3) {
                return "No changes in eco impact.";
            }
        }


        double waterSaved = totalPoints * 0.1;
        double co2Saved = totalPoints * 0.05;
        double energySaved = totalPoints * 0.2;
        double wasteReduced = totalPoints * 0.3;


        EcoImpactHistory historyRecord = new EcoImpactHistory();
        historyRecord.setUser(user);
        historyRecord.setWaterSaved(waterSaved);
        historyRecord.setCo2Saved(co2Saved);
        historyRecord.setEnergySaved(energySaved);
        historyRecord.setWasteReduced(wasteReduced);
        historyRecord.setDate(LocalDate.now());
        historyRecord.setTotalPoints(totalPoints);

        ecoImpactHistoryRepository.save(historyRecord);

        return String.format("You have saved %.2f liters of water, %.2f kg of CO2, %.2f kWh of energy, and %.2f kg of waste.", waterSaved, co2Saved, energySaved, wasteReduced);
    }


    public List<EcoImpactHistory> getEcoImpactHistory(Long userId) {
        return ecoImpactHistoryRepository.findByUserId(userId);
    }
}
