package projekt.zespolowy.zero_waste.dto;

import lombok.Data;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;

import java.util.List;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private List<String> tags;
    private ArticleCategory articleCategory;
    private String authorUsername;
    private boolean likedByCurrentUser; // Nowe pole
    private int likesCount; // Nowe pole
}
