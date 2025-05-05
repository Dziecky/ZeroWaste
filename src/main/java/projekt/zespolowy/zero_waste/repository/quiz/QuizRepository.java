package projekt.zespolowy.zero_waste.repository.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.quiz.Quiz;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Find quizzes created by a specific user
    List<Quiz> findByCreator(User creator);

    // Future: Maybe add methods for searching quizzes by title, etc.
} 