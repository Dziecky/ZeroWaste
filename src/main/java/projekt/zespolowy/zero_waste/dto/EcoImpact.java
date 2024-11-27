package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EcoImpact {
    private double waterSaved;
    private double co2Saved;
    private double energySaved;
    private double wasteReduced;
}

