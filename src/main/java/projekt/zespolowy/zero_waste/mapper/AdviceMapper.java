package projekt.zespolowy.zero_waste.mapper;

import org.mapstruct.*;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.Tag;
import projekt.zespolowy.zero_waste.services.TagService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdviceMapper {

    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "author", ignore = true) // Autor ustawiany w serwisie
    Advice toEntity(AdviceDTO dto, @Context TagService tagService);

    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(target = "tags", ignore = true)
    AdviceDTO toDTO(Advice advice);

    @Mapping(target = "tags", ignore = true)
    void updateAdviceFromDTO(AdviceDTO dto, @MappingTarget Advice advice, @Context TagService tagService);

    @AfterMapping
    default void afterToEntity(AdviceDTO dto, @MappingTarget Advice advice, @Context TagService tagService) {
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            Set<Tag> tags = dto.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            advice.setTags(tags);
        }
    }

    @AfterMapping
    default void afterToDTO(Advice advice, @MappingTarget AdviceDTO dto) {
        if (advice.getTags() != null && !advice.getTags().isEmpty()) {
            List<String> tagNames = advice.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            dto.setTags(tagNames);
        }
    }

    @AfterMapping
    default void afterUpdateAdviceFromDTO(AdviceDTO dto, @MappingTarget Advice advice, @Context TagService tagService) {
        if (dto.getTags() != null) {
            Set<Tag> newTags = dto.getTags().stream()
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            advice.setTags(newTags);
        }
    }
}
