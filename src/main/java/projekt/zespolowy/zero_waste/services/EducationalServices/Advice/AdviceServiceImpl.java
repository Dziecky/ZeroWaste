package projekt.zespolowy.zero_waste.services.EducationalServices.Advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;
import projekt.zespolowy.zero_waste.services.TagService;

import java.util.Optional;

@Service
public class AdviceServiceImpl implements AdviceService {
    private final AdviceRepository adviceRepository;
    private final TagService tagService;
    private final AdviceMapper adviceMapper;
    @Autowired
    public AdviceServiceImpl(AdviceRepository adviceRepository, TagService tagService, AdviceMapper adviceMapper) {
        this.adviceRepository = adviceRepository;
        this.tagService = tagService;
        this.adviceMapper = adviceMapper;
    }
    @Override
    public Advice createAdvice(AdviceDTO adviceDTO) {
        Advice advice = adviceMapper.toEntity(adviceDTO, tagService);
        return adviceRepository.save(advice);
    }
    @Override
    public Advice updateAdvice(Long id, AdviceDTO adviceDTO) {
        return adviceRepository.findById(id).map(existingAdvice -> {
            adviceMapper.afterUpdateAdviceFromDTO(adviceDTO, existingAdvice, tagService);
            return adviceRepository.save(existingAdvice);
        }).orElseThrow(() -> new RuntimeException("Advice not found with id " + id));
    }

    @Override
    public Optional<Advice> getAdviceById(Long id) {
        return adviceRepository.findById(id);
    }
    @Override
    public Page<Advice> getAllAdvices(Pageable pageable) {
        return adviceRepository.findAll(pageable);
    }
    @Override
    public Page<Advice> getAdvicesByTitle(String title, Pageable pageable) {
        if(title != null && !title.trim().isEmpty()){
            return adviceRepository.findByTitleContainingIgnoreCase(title, pageable);
        }
        else {
            return adviceRepository.findAll(pageable);
        }
    }

    @Override
    public void deleteAdvice(Long id) {
        adviceRepository.deleteById(id);
    }

    @Override
    public Page<Advice> getAdvicesByCategory(AdviceCategory category, Pageable pageable) {
        if(category!=null){
            return adviceRepository.findByAdviceCategory(category, pageable);
        }else{
            return adviceRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Advice> findByTags_NameIgnoreCase(String tagName, Pageable pageable) {
        if(tagName != null && !tagName.trim().isEmpty()){
            return adviceRepository.findByTags_NameIgnoreCase(tagName, pageable);
        }
        else {
            return adviceRepository.findAll(pageable);
        }
    }
    @Override
    public Page<Advice> findAdvices(AdviceCategory category, String title, String tagName, Pageable pageable) {
        Specification<Advice> spec = Specification.where(null);
        if(category != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("adviceCategory"), category));
        }
        if(title != null && !title.trim().isEmpty()){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if(tagName != null && !tagName.trim().isEmpty()){
            spec = spec.and((root, query, cb) -> cb.equal(root.join("tags").get("name"), tagName));
        }
        return adviceRepository.findAll(spec, pageable);
    }
}
