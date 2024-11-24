package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Tag;
import projekt.zespolowy.zero_waste.repository.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Tag createTag(Tag tag) {
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tag.getName());
        return existingTag.orElseGet(() -> tagRepository.save(tag));
    }

    @Override
    public Tag updateTag(Long id, Tag tagDetails) {
        return tagRepository.findById(id).map(tag -> {
            tag.setName(tagDetails.getName());
            return tagRepository.save(tag);
        }).orElseThrow(() -> new RuntimeException("Tag not found with id " + id));
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
