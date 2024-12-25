package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ReportDto {
    private double waterSaved;
    private double co2Saved;
    private double energySaved;
    private double wasteReduced;
    private LocalDate reportDate;
}
