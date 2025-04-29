package projekt.zespolowy.zero_waste.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerDto {
    private Long id;
    private String text;
    private boolean isCorrect; // Needed when creating/editing quiz

    // Constructor for creating/editing
    public AnswerDto(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    // Constructor for display (e.g., when taking quiz)
    public AnswerDto(Long id, String text) {
        this.id = id;
        this.text = text;
        this.isCorrect = false; // Don't expose correctness when taking quiz
    }


}