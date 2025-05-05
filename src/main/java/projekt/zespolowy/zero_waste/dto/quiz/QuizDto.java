package projekt.zespolowy.zero_waste.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    private Long id;

    @NotEmpty(message = "Quiz title cannot be empty")
    private String title;

    private String description;

    private String creatorUsername; // To display creator info

    @Valid // Ensure nested QuestionDtos are validated
    @Size(min = 1, message = "Quiz must have at least one question")
    private List<QuestionDto> questions = new ArrayList<>();

    // Constructor for listing quizzes (less detail)
    public QuizDto(Long id, String title, String description, String creatorUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creatorUsername = creatorUsername;
        this.questions = null; // Don't include questions in list view
    }

    // Default constructor is provided by @NoArgsConstructor

} 