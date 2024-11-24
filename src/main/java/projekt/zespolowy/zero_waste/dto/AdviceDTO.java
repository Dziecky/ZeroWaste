package projekt.zespolowy.zero_waste.dto;

import lombok.Getter;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;

import java.util.List;

@Getter
@Setter
public class AdviceDTO {

    private String title;

    private String content;

    private List<String> tags;

    private AdviceCategory adviceCategory;
}
