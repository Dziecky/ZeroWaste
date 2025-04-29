package projekt.zespolowy.zero_waste.services.quiz;

import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private SubmittedAnswerRepository submittedAnswerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private QuizService quizService;

    private User testUser;
    private QuizDto quizDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        AnswerDto answerDto1 = new AnswerDto("Answer 1", false);
        AnswerDto answerDto2 = new AnswerDto("Answer 2", false);
        QuestionDto questionDto1 = new QuestionDto(null, "Question 1?", List.of(answerDto1, answerDto2), 1);

        AnswerDto answerDto3 = new AnswerDto("Ans 3", false);
        AnswerDto answerDto4 = new AnswerDto("Ans 4", false);
        QuestionDto questionDto2 = new QuestionDto(null, "Question 2?", List.of(answerDto3, answerDto4), 0);

        quizDto = new QuizDto(null, "Test Quiz", "Test Description", null, List.of(questionDto1, questionDto2));
    }

    @Test
    void createQuiz_Success() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz quizToSave = invocation.getArgument(0);
            quizToSave.setId(100L);
            long qId = 1;
            long aId = 1;
            for (Question q : quizToSave.getQuestions()) {
                q.setId(qId++);
                q.setQuiz(quizToSave);
                for (Answer a : q.getAnswers()) {
                    a.setId(aId++);
                    a.setQuestion(q);
                }
            }
            return quizToSave;
        });

        Quiz createdQuiz = quizService.createQuiz(quizDto);

        assertNotNull(createdQuiz);
        assertEquals(quizDto.getTitle(), createdQuiz.getTitle());
        assertEquals(quizDto.getDescription(), createdQuiz.getDescription());
        assertEquals(testUser, createdQuiz.getCreator());
        assertEquals(2, createdQuiz.getQuestions().size());
        assertNotNull(createdQuiz.getId());

        Question q1 = createdQuiz.getQuestions().get(0);
        assertEquals("Question 1?", q1.getText());
        assertEquals(2, q1.getAnswers().size());
        assertFalse(q1.getAnswers().get(0).isCorrect(), "Answer 1 should be incorrect");
        assertTrue(q1.getAnswers().get(1).isCorrect(), "Answer 2 should be correct");

        Question q2 = createdQuiz.getQuestions().get(1);
        assertEquals("Question 2?", q2.getText());
        assertEquals(2, q2.getAnswers().size());
        assertTrue(q2.getAnswers().get(0).isCorrect(), "Answer 3 should be correct");
        assertFalse(q2.getAnswers().get(1).isCorrect(), "Answer 4 should be incorrect");

        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void createQuiz_UserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            quizService.createQuiz(quizDto);
        });

        assertEquals("User must be logged in to create a quiz.", exception.getMessage());

        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void getAllQuizzes_ReturnsListOfQuizDtos() {
        User creator1 = new User(); creator1.setUsername("user1");
        User creator2 = new User(); creator2.setUsername("user2");

        Quiz quiz1 = new Quiz(); quiz1.setId(1L); quiz1.setTitle("Quiz 1"); quiz1.setDescription("Desc 1"); quiz1.setCreator(creator1);
        Quiz quiz2 = new Quiz(); quiz2.setId(2L); quiz2.setTitle("Quiz 2"); quiz2.setDescription("Desc 2"); quiz2.setCreator(creator2);
        List<Quiz> quizzes = List.of(quiz1, quiz2);

        when(quizRepository.findAll()).thenReturn(quizzes);

        List<QuizDto> result = quizService.getAllQuizzes();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Quiz 1", result.get(0).getTitle());
        assertEquals("Desc 1", result.get(0).getDescription());
        assertEquals("user1", result.get(0).getCreatorUsername());
        assertNull(result.get(0).getQuestions(), "Questions should not be included in list view");

        assertEquals(2L, result.get(1).getId());
        assertEquals("Quiz 2", result.get(1).getTitle());
        assertEquals("Desc 2", result.get(1).getDescription());
        assertEquals("user2", result.get(1).getCreatorUsername());
        assertNull(result.get(1).getQuestions(), "Questions should not be included in list view");

        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getAllQuizzes_ReturnsEmptyList() {
        when(quizRepository.findAll()).thenReturn(List.of());

        List<QuizDto> result = quizService.getAllQuizzes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getQuizForTaking_Success() {
        Long quizId = 1L;
        Quiz quiz = createFullQuizEntity(quizId, testUser);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizDto resultDto = quizService.getQuizForTaking(quizId);

        assertNotNull(resultDto);
        assertEquals(quizId, resultDto.getId());
        assertEquals(quiz.getTitle(), resultDto.getTitle());
        assertEquals(quiz.getDescription(), resultDto.getDescription());
        assertEquals(testUser.getUsername(), resultDto.getCreatorUsername());
        assertNotNull(resultDto.getQuestions());
        assertEquals(1, resultDto.getQuestions().size());

        QuestionDto questionDto = resultDto.getQuestions().get(0);
        assertNotNull(questionDto.getAnswers());
        assertEquals(2, questionDto.getAnswers().size());
        assertFalse(questionDto.getAnswers().get(0).isCorrect(), "Correctness should be hidden");
        assertFalse(questionDto.getAnswers().get(1).isCorrect(), "Correctness should be hidden");
        assertNull(questionDto.getCorrectAnswerIndex(), "Correct index should not be exposed");

        verify(quizRepository, times(1)).findById(quizId);
    }

    @Test
    void getQuizForTaking_NotFound() {
        Long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            quizService.getQuizForTaking(quizId);
        });

        assertEquals("Quiz not found with id: " + quizId, exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
    }

    @Test
    void getQuizForEditing_Success() {
        // Arrange
        Long quizId = 1L;
        Quiz quiz = createFullQuizEntity(quizId, testUser); // Use helper

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userService.getCurrentUser()).thenReturn(testUser); // User is the creator

        // Act
        QuizDto resultDto = quizService.getQuizForEditing(quizId);

        // Assert
        assertNotNull(resultDto);
        assertEquals(quizId, resultDto.getId());
        assertEquals(quiz.getTitle(), resultDto.getTitle());
        assertEquals(testUser.getUsername(), resultDto.getCreatorUsername()); // Mapper should set this
        assertNotNull(resultDto.getQuestions());
        assertEquals(1, resultDto.getQuestions().size());

        // Verify answers include correctness
        QuestionDto questionDto = resultDto.getQuestions().get(0);
        assertNotNull(questionDto.getAnswers());
        assertEquals(2, questionDto.getAnswers().size());
        assertTrue(questionDto.getAnswers().get(0).isCorrect(), "Correctness should be included for editing");
        assertFalse(questionDto.getAnswers().get(1).isCorrect(), "Correctness should be included for editing");
        // Check if the correct answer index is set by the mapper
        assertNotNull(questionDto.getCorrectAnswerIndex(), "Correct index should be set for editing");
        assertEquals(0, questionDto.getCorrectAnswerIndex(), "Index 0 should be marked as correct");

        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void getQuizForEditing_NotFound() {
        // Arrange
        Long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());
        // No need to mock userService as it throws before the check

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            quizService.getQuizForEditing(quizId);
        });

        assertEquals("Quiz not found with id: " + quizId, exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, never()).getCurrentUser(); // Verify auth check wasn't reached
    }

    @Test
    void getQuizForEditing_Unauthorized() {
        // Arrange
        Long quizId = 1L;
        User creator = new User(); creator.setId(5L); creator.setUsername("creator");
        Quiz quiz = createFullQuizEntity(quizId, creator); // Quiz created by different user
        User differentUser = new User(); differentUser.setId(6L); differentUser.setUsername("hacker");

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userService.getCurrentUser()).thenReturn(differentUser); // Current user is not the creator

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            quizService.getQuizForEditing(quizId);
        });

        assertEquals("You are not authorized to edit this quiz.", exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void deleteQuiz_Success() {
        // Arrange
        Long quizId = 1L;
        Quiz quiz = createFullQuizEntity(quizId, testUser); // Quiz created by testUser

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userService.getCurrentUser()).thenReturn(testUser); // Current user is the creator
        doNothing().when(quizRepository).delete(quiz); // Mock void method

        // Act
        assertDoesNotThrow(() -> {
             quizService.deleteQuiz(quizId);
        });

        // Assert
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void deleteQuiz_NotFound() {
        // Arrange
        Long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            quizService.deleteQuiz(quizId);
        });

        assertEquals("Quiz not found with id: " + quizId, exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, never()).getCurrentUser();
        verify(quizRepository, never()).delete(any(Quiz.class));
    }

    @Test
    void deleteQuiz_Unauthorized() {
        // Arrange
        Long quizId = 1L;
        User creator = new User(); creator.setId(5L); creator.setUsername("creator");
        Quiz quiz = createFullQuizEntity(quizId, creator);
        User differentUser = new User(); differentUser.setId(6L); differentUser.setUsername("hacker");

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userService.getCurrentUser()).thenReturn(differentUser);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            quizService.deleteQuiz(quizId);
        });

        assertEquals("You are not authorized to delete this quiz.", exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, never()).delete(any(Quiz.class));
    }

    @Test
    void updateQuiz_Success() {
        // Arrange
        Long quizId = 1L;
        Quiz existingQuiz = createFullQuizEntity(quizId, testUser);
        // Create a DTO with updated info
        AnswerDto updatedAnswer1 = new AnswerDto(null, "Updated Answer 1", false);
        AnswerDto updatedAnswer2 = new AnswerDto(null, "Updated Correct Answer 2", false);
        QuestionDto updatedQuestion = new QuestionDto(null, "Updated Question?", List.of(updatedAnswer1, updatedAnswer2), 1); // Index 1 correct
        QuizDto updateDto = new QuizDto(quizId, "Updated Title", "Updated Desc", null, List.of(updatedQuestion));

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(existingQuiz));
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved quiz

        // Act
        Quiz updatedQuiz = quizService.updateQuiz(quizId, updateDto);

        // Assert
        assertNotNull(updatedQuiz);
        assertEquals(quizId, updatedQuiz.getId()); // ID should remain the same
        assertEquals("Updated Title", updatedQuiz.getTitle());
        assertEquals("Updated Desc", updatedQuiz.getDescription());
        assertEquals(testUser, updatedQuiz.getCreator()); // Creator should not change

        // Verify questions were replaced
        assertEquals(1, updatedQuiz.getQuestions().size());
        Question savedQuestion = updatedQuiz.getQuestions().get(0);
        assertEquals("Updated Question?", savedQuestion.getText());
        assertEquals(2, savedQuestion.getAnswers().size());
        assertEquals("Updated Answer 1", savedQuestion.getAnswers().get(0).getText());
        assertFalse(savedQuestion.getAnswers().get(0).isCorrect());
        assertEquals("Updated Correct Answer 2", savedQuestion.getAnswers().get(1).getText());
        assertTrue(savedQuestion.getAnswers().get(1).isCorrect());

        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
        // Verify save is called on the modified existingQuiz instance
        verify(quizRepository, times(1)).save(existingQuiz);
    }

    @Test
    void updateQuiz_NotFound() {
        // Arrange
        Long quizId = 99L;
        QuizDto updateDto = new QuizDto(quizId, "Update Title", "Desc", null, List.of()); // DTO content doesn't matter here

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            quizService.updateQuiz(quizId, updateDto);
        });

        assertEquals("Quiz not found with id: " + quizId, exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, never()).getCurrentUser();
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void updateQuiz_Unauthorized() {
         // Arrange
        Long quizId = 1L;
        User creator = new User(); creator.setId(5L); creator.setUsername("creator");
        Quiz existingQuiz = createFullQuizEntity(quizId, creator);
        User differentUser = new User(); differentUser.setId(6L); differentUser.setUsername("hacker");
        QuizDto updateDto = new QuizDto(quizId, "Update Title", "Desc", null, List.of());

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(existingQuiz));
        when(userService.getCurrentUser()).thenReturn(differentUser);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            quizService.updateQuiz(quizId, updateDto);
        });

        assertEquals("You are not authorized to update this quiz.", exception.getMessage());
        verify(quizRepository, times(1)).findById(quizId);
        verify(userService, times(1)).getCurrentUser();
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    private Quiz createFullQuizEntity(Long quizId, User creator) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Sample Quiz");
        quiz.setDescription("Sample Desc");
        quiz.setCreator(creator);

        Question question = new Question();
        question.setId(10L);
        question.setText("Sample Question?");

        Answer answer1 = new Answer();
        answer1.setId(100L);
        answer1.setText("Correct Ans");
        answer1.setCorrect(true);
        question.addAnswer(answer1);

        Answer answer2 = new Answer();
        answer2.setId(101L);
        answer2.setText("Wrong Ans");
        answer2.setCorrect(false);
        question.addAnswer(answer2);

        quiz.addQuestion(question);
        return quiz;
    }
} 