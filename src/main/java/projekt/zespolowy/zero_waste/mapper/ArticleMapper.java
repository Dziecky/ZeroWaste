package projekt.zespolowy.zero_waste.mapper;

import org.mapstruct.*;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.Tag;
import projekt.zespolowy.zero_waste.services.TagService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "tags", ignore = true)
    Article toEntity(ArticleDTO dto, @Context TagService tagService);

    @Mapping(target = "tags", ignore = true)
    ArticleDTO toDTO(Article Article);

    @Mapping(target = "tags", ignore = true)
    void updateArticleFromDTO(ArticleDTO dto, @MappingTarget Article Article, @Context TagService tagService);

    @AfterMapping
    default void afterToEntity(ArticleDTO dto, @MappingTarget Article Article, @Context TagService tagService) {
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            Set<Tag> tags = dto.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            Article.setTags(tags);
        }
    }

    @AfterMapping
    default void afterToDTO(Article Article, @MappingTarget ArticleDTO dto) {
        if (Article.getTags() != null && !Article.getTags().isEmpty()) {
            List<String> tagNames = Article.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            dto.setTags(tagNames);
        }
    }

    @AfterMapping
    default void afterUpdateArticleFromDTO(ArticleDTO dto, @MappingTarget Article Article, @Context TagService tagService) {
        if (dto.getTags() != null) {
            Set<Tag> newTags = dto.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            Article.setTags(newTags);
        }
    }
}
