package projekt.zespolowy.zero_waste.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.Comment;

import java.util.List;

@Getter
@Setter
public class ThreadResponseDTO {
    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;

    @NotBlank(message = "Treść nie może być pusta")
    private String content;

    private User author;

    private List<Comment> comments;
}
