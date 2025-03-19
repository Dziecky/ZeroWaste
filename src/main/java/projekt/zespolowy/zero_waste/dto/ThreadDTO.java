package projekt.zespolowy.zero_waste.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThreadDTO {
    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;

    @NotBlank(message = "Treść nie może być pusta")
    private String content;
}
