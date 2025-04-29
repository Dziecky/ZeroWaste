package projekt.zespolowy.zero_waste.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDto {
    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private String username;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime completedAt;
    private List<SubmittedAnswerDto> results; // List of submitted answers with correctness details
} 