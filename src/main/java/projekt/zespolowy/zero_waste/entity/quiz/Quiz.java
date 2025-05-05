package projekt.zespolowy.zero_waste.entity.quiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    // Helper method to add questions
    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    // Helper method to remove questions
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }
} 