package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query(value = "SELECT * FROM challenges ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Challenge findRandomChallenge();
}
