package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;

import java.util.List;

public interface TipRepository extends JpaRepository<Tip, Long> {

    @Query(value = "SELECT * FROM tips ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Tip findRandomTip();

}
