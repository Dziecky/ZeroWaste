package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.Tag;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
}
