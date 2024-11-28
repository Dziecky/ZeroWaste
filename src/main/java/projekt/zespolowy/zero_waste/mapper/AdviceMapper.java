// src/main/java/projekt/zespolowy/zero_waste/mapper/AdviceMapper.java

package projekt.zespolowy.zero_waste.mapper;

import org.mapstruct.*;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.Tag;
import projekt.zespolowy.zero_waste.services.TagService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TagService.class)
public interface AdviceMapper {

    @Mapping(target = "tags", ignore = true) // Handle tags in after-mapping
    Advice toEntity(AdviceDTO dto, @Context TagService tagService);

    @Mapping(target = "tags", ignore = true) // Handle tags in after-mapping
    AdviceDTO toDTO(Advice advice);

    // Metoda pomocnicza do mapowania tagów
    default Set<Tag> mapTags(List<String> tagNames, @Context TagService tagService) {
        if (tagNames == null) {
            return Set.of();
        }
        return tagNames.stream()
                .map(tagName -> tagService.getTagByName(tagName)
                        .orElseGet(() -> tagService.createTag(new Tag(tagName))))
                .collect(Collectors.toSet());
    }

    // Uses the mapTags helper method to convert the DTO's list of tag names into a set of Tag entities
    @AfterMapping
    default void afterToEntity(AdviceDTO dto, @MappingTarget Advice advice, @Context TagService tagService) {
        Set<Tag> tags = mapTags(dto.getTags(), tagService);
        for (Tag tag : tags) {
            advice.addTag(tag); // Use the addTag method to maintain bidirectional relationship
        }
    }

    // Converts the entity's set of Tag entities into a list of tag names.
    @AfterMapping
    default void afterToDTO(Advice advice, @MappingTarget AdviceDTO dto) {
        List<String> tagNames = advice.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        dto.setTags(tagNames);
    }

    @AfterMapping
    default void afterUpdateAdviceFromDTO(AdviceDTO dto, @MappingTarget Advice advice, @Context TagService tagService) {
        Set<Tag> newTags = mapTags(dto.getTags(), tagService);
        // Remove tags that are no longer associated
        Set<Tag> tagsToRemove = new HashSet<>(advice.getTags());
        tagsToRemove.removeAll(newTags);
        for (Tag tag : tagsToRemove) {
            advice.removeTag(tag);
        }
        // Add new tags
        for (Tag tag : newTags) {
            if (!advice.getTags().contains(tag)) {
                advice.addTag(tag);
            }
        }
    }


}
