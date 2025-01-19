package projekt.zespolowy.zero_waste.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;

import java.util.Date;
import java.util.List;

@Data
public class AdviceDTO {
    private Long id;

    private String title;

    private String content;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
    private AdviceCategory adviceCategory;
    private List<String> tags;
    private String authorUsername;
    private boolean likedByCurrentUser;
    private int likesCount;
}
