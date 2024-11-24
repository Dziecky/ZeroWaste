package projekt.zespolowy.zero_waste.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;

import java.util.Optional;

public interface AdviceService {
    Page<Advice> getAllAdvices(Pageable pageable);
    Advice createAdvice(AdviceDTO adviceDTO);
    Advice updateAdvice(Long id, AdviceDTO adviceDTO);
    Optional<Advice> getAdviceById(Long id);
    void deleteAdvice(Long id);
    Page<Advice> getAdvicesByCategory(AdviceCategory category, Pageable pageable);
    Page<Advice> getAdvicesByTitle(String title, Pageable pageable);
}
