package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;

import java.util.Optional;

@Service
public class AdviceServiceImpl implements AdviceService {
    private final AdviceRepository adviceRepository;
    @Autowired
    public AdviceServiceImpl(AdviceRepository adviceRepository) {
        this.adviceRepository = adviceRepository;
    }
    @Override
    public Advice saveAdvice(Advice advice) {
        return adviceRepository.save(advice);
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
        return adviceRepository.findByTitleContainingIgnoreCase(title, pageable);
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
