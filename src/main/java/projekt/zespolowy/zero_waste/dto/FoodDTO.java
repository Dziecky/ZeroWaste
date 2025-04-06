package projekt.zespolowy.zero_waste.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FoodDTO {
    private String productName;
    private String brand;
    private String nutriScore;
    private String ecoScore;
    private String ingredients;
    private String imageUrl;
}
