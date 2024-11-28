package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {
    Page<Advice> findByAdviceCategory(AdviceCategory category, Pageable pageable);
    Page<Advice> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
