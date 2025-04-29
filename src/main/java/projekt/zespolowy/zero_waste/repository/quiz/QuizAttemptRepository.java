package projekt.zespolowy.zero_waste.repository.quiz;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.quiz.Quiz;
import projekt.zespolowy.zero_waste.entity.quiz.QuizAttempt;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // Find attempts for a specific quiz by a specific user
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);

    // Find all attempts by a specific user
    List<QuizAttempt> findByUser(User user);

    // Find the latest attempt for a specific quiz by a specific user
    Optional<QuizAttempt> findTopByUserAndQuizOrderByCompletedAtDesc(User user, Quiz quiz);

    // Override findById or create a new method to use the Entity Graph
    @Override
    @EntityGraph(value = "QuizAttempt.withDetails")
    Optional<QuizAttempt> findById(Long id);

    // Alternatively, define a new method if you don't want to override:
    // @EntityGraph(value = "QuizAttempt.withDetails")
    // Optional<QuizAttempt> findByIdWithDetails(Long id);
} 