package projekt.zespolowy.zero_waste.repository.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.quiz.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Currently no specific query methods needed, basic CRUD is sufficient
} 