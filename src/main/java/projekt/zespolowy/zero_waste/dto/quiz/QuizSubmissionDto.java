package projekt.zespolowy.zero_waste.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class QuizSubmissionDto {
    // Key: Question ID, Value: Selected Answer ID
    private Map<Long, Long> answers = new HashMap<>(); // Initialize to avoid null pointer exceptions
    
    // Helper method to add an answer
    public void addAnswer(Long questionId, Long answerId) {
        if (answers == null) {
            answers = new HashMap<>();
        }
        answers.put(questionId, answerId);
    }
} 