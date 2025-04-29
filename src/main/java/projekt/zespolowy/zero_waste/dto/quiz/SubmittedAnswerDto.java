package projekt.zespolowy.zero_waste.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedAnswerDto {
    private Long questionId;
    private String questionText;
    private Long selectedAnswerId; // ID of the answer the user selected
    private String selectedAnswerText;
    private Long correctAnswerId; // ID of the correct answer
    private String correctAnswerText;
    private boolean isCorrect; // Was the submitted answer correct?
} 