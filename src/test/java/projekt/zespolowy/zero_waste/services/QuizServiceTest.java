package projekt.zespolowy.zero_waste.services;

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
import projekt.zespolowy.zero_waste.services.quiz.QuizService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private Quiz testQuiz; // Dodane pole testQuiz

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

        // Setup a basic Quiz entity for retrieval tests
        Quiz quiz = new Quiz();
        quiz.setId(100L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        User creator = new User();
        creator.setUsername("testUser");
        quiz.setCreator(creator);
        Question question1 = new Question();
        question1.setText("Question 1?");
        Answer answer1 = new Answer();
        answer1.setText("Answer 1");
        answer1.setCorrect(false);
        Answer answer2 = new Answer();
        answer2.setText("Answer 2");
        answer2.setCorrect(true);
        question1.setAnswers(List.of(answer1, answer2));
        Question question2 = new Question();
        question2.setText("Question 2?");
        Answer answer3 = new Answer();
        answer3.setText("Ans 3");
        answer3.setCorrect(true);
        Answer answer4 = new Answer();
        answer4.setText("Ans 4");
        answer4.setCorrect(false);
        question2.setAnswers(List.of(answer3, answer4));
        quiz.setQuestions(List.of(question1, question2));
        testQuiz = quiz;
    }



    @Test
    void createQuiz_UserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> quizService.createQuiz(quizDto));
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void getAllQuizzes_Success() {
        when(quizRepository.findAll()).thenReturn(List.of(testQuiz));

        List<QuizDto> allQuizzes = quizService.getAllQuizzes();

        assertFalse(allQuizzes.isEmpty());
        assertEquals(1, allQuizzes.size());
        assertEquals(testQuiz.getId(), allQuizzes.get(0).getId());
        assertEquals(testQuiz.getTitle(), allQuizzes.get(0).getTitle());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getQuizForTaking_Success() {
        when(quizRepository.findById(100L)).thenReturn(Optional.of(testQuiz));

        QuizDto quizForTaking = quizService.getQuizForTaking(100L);

        assertNotNull(quizForTaking);
        assertEquals(testQuiz.getId(), quizForTaking.getId());
        assertEquals(2, quizForTaking.getQuestions().size());
        assertFalse(quizForTaking.getQuestions().get(0).getAnswers().get(0).isCorrect());
        assertFalse(quizForTaking.getQuestions().get(0).getAnswers().get(1).isCorrect());
        verify(quizRepository, times(1)).findById(100L);
    }

    @Test
    void getQuizForTaking_NotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.getQuizForTaking(1L));
        verify(quizRepository, times(1)).findById(1L);
    }



    @Test
    void getQuizForEditing_NotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.getQuizForEditing(1L));
        verify(quizRepository, times(1)).findById(1L);
        verify(userService, never()).getCurrentUser();
    }

    @Test
    void getQuizForEditing_Unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(quizRepository.findById(100L)).thenReturn(Optional.of(testQuiz));
        when(userService.getCurrentUser()).thenReturn(anotherUser);

        assertThrows(SecurityException.class, () -> quizService.getQuizForEditing(100L));
        verify(quizRepository, times(1)).findById(100L);
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void updateQuiz_NotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.updateQuiz(1L, quizDto));
        verify(quizRepository, times(1)).findById(1L);
        verify(userService, never()).getCurrentUser();
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void deleteQuiz_NotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.deleteQuiz(1L));
        verify(quizRepository, times(1)).findById(1L);
        verify(userService, never()).getCurrentUser();
        verify(quizRepository, never()).delete(any());
    }
}