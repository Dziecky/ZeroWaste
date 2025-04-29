package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projekt.zespolowy.zero_waste.dto.quiz.AnswerDto;
import projekt.zespolowy.zero_waste.dto.quiz.QuestionDto;
import projekt.zespolowy.zero_waste.dto.quiz.QuizDto;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.quiz.Answer;
import projekt.zespolowy.zero_waste.entity.quiz.Question;
import projekt.zespolowy.zero_waste.entity.quiz.Quiz;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.quiz.*;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.quiz.QuizService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuestionRepository questionRepository; // Although likely managed by cascade
    @Mock
    private AnswerRepository answerRepository;     // Although likely managed by cascade
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private SubmittedAnswerRepository submittedAnswerRepository;
    @Mock
    private UserRepository userRepository; // May not be needed if UserService handles user fetching
    @Mock
    private UserService userService;

    @InjectMocks
    private QuizService quizService;

    private User testUser;
    private QuizDto quizDto;

    @BeforeEach
    void setUp() {
        // Common setup for tests
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        // Setup a basic QuizDto for creation tests
        AnswerDto answerDto1 = new AnswerDto("Answer 1", false);
        AnswerDto answerDto2 = new AnswerDto("Answer 2", false);
        QuestionDto questionDto1 = new QuestionDto(null, "Question 1?", List.of(answerDto1, answerDto2), 1);

        AnswerDto answerDto3 = new AnswerDto("Ans 3", false);
        AnswerDto answerDto4 = new AnswerDto("Ans 4", false);
        QuestionDto questionDto2 = new QuestionDto(null, "Question 2?", List.of(answerDto3, answerDto4), 0);

        quizDto = new QuizDto(null, "Test Quiz", "Test Description", null, List.of(questionDto1, questionDto2));
        quizDto.setCreatorUsername(testUser.getUsername()); // Although service uses logged-in user
    }

    @Test
    void createQuiz_Success() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testUser);
        // Mock the save operation to return the quiz with an ID (important!)
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz quizToSave = invocation.getArgument(0);
            quizToSave.setId(100L); // Simulate saving and getting an ID
            // Simulate cascade saving for questions/answers (assign dummy IDs if needed for verification)
            long qId = 1;
            long aId = 1;
            for (Question q : quizToSave.getQuestions()) {
                q.setId(qId++);
                q.setQuiz(quizToSave); // Ensure back-reference is set if logic depends on it
                for (Answer a : q.getAnswers()) {
                    a.setId(aId++);
                    a.setQuestion(q);
                }
            }
            return quizToSave;
        });

        // Act
        Quiz createdQuiz = quizService.createQuiz(quizDto);

        // Assert
        assertNotNull(createdQuiz);
        assertEquals(quizDto.getTitle(), createdQuiz.getTitle());
        assertEquals(quizDto.getDescription(), createdQuiz.getDescription());
        assertEquals(testUser, createdQuiz.getCreator());
        assertEquals(2, createdQuiz.getQuestions().size());
        assertNotNull(createdQuiz.getId()); // Ensure ID was assigned

        // Verify question 1
        Question q1 = createdQuiz.getQuestions().get(0);
        assertEquals("Question 1?", q1.getText());
        assertEquals(2, q1.getAnswers().size());
        assertFalse(q1.getAnswers().get(0).isCorrect(), "Answer 1 should be incorrect");
        assertTrue(q1.getAnswers().get(1).isCorrect(), "Answer 2 should be correct");

        // Verify question 2
        Question q2 = createdQuiz.getQuestions().get(1);
        assertEquals("Question 2?", q2.getText());
        assertEquals(2, q2.getAnswers().size());
        assertTrue(q2.getAnswers().get(0).isCorrect(), "Answer 3 should be correct");
        assertFalse(q2.getAnswers().get(1).isCorrect(), "Answer 4 should be incorrect");


        // Verify that save was called
        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void createQuiz_UserNotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            quizService.createQuiz(quizDto);
        });

        assertEquals("User must be logged in to create a quiz.", exception.getMessage());

        // Verify that save was never called
        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    // --- Add more tests here for other methods and scenarios ---

} 