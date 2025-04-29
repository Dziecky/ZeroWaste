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
public class QuestionDto {
    private Long id;

    @NotEmpty(message = "Question text cannot be empty")
    private String text;

    @Valid // Ensure nested AnswerDtos are validated
    @Size(min = 2, message = "Each question must have at least 2 answers") // Example validation
    private List<AnswerDto> answers = new ArrayList<>();

    // This field will capture the index submitted by the radio button group for this question
    private Integer correctAnswerIndex;



}