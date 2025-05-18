package projekt.zespolowy.zero_waste.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ArticleCommentDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
}
