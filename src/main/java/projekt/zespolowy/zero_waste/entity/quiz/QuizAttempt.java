package projekt.zespolowy.zero_waste.entity.quiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Add NamedEntityGraph definition
@NamedEntityGraph(
    name = "QuizAttempt.withDetails",
    attributeNodes = {
        @NamedAttributeNode(value = "user"), // Eagerly fetch user
        @NamedAttributeNode(value = "quiz"), // Eagerly fetch quiz
        @NamedAttributeNode(value = "submittedAnswers", subgraph = "submittedAnswersGraph") // Fetch submitted answers using a subgraph
    },
    subgraphs = {
        @NamedSubgraph(
            name = "submittedAnswersGraph",
            attributeNodes = {
                @NamedAttributeNode("question"), // Eagerly fetch question within submitted answer
                @NamedAttributeNode("selectedAnswer") // <<-- Eagerly fetch the selected answer
            }
        )
    }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private Integer score;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SubmittedAnswer> submittedAnswers = new HashSet<>();

    // Helper method
    public void addSubmittedAnswer(SubmittedAnswer submittedAnswer) {
        submittedAnswers.add(submittedAnswer);
        submittedAnswer.setQuizAttempt(this);
    }
} 