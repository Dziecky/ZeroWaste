package projekt.zespolowy.zero_waste.controller.quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import projekt.zespolowy.zero_waste.dto.quiz.*;
import projekt.zespolowy.zero_waste.entity.quiz.Quiz;
import projekt.zespolowy.zero_waste.services.quiz.QuizService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    private QuizDto testQuizDto;
    private QuizAttemptDto testAttemptDto;

    @BeforeEach
    void setUp() {
        testQuizDto = new QuizDto();
        testQuizDto.setId(1L);
        testQuizDto.setTitle("Test Quiz");
        testQuizDto.setDescription("Test Description");
        testQuizDto.setCreatorUsername("testUser");

        testAttemptDto = new QuizAttemptDto(
            1L, 1L, "Test Quiz", "testUser",
            5, 10, LocalDateTime.now(), Collections.emptyList()
        );
    }

    @Test
    @WithMockUser(username = "testUser")
    void listQuizzes_ReturnsQuizListPage() throws Exception {
        List<QuizDto> quizzes = Collections.singletonList(testQuizDto);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/list"))
                .andExpect(model().attributeExists("quizzes"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void takeQuiz_ReturnsQuizPage() throws Exception {
        when(quizService.getQuizForTaking(1L)).thenReturn(testQuizDto);

        mockMvc.perform(get("/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/take"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attributeExists("submissionDto"));
    }





    @Test
    @WithMockUser(username = "testUser")
    void listUserAttempts_ReturnsAttemptsPage() throws Exception {
        List<QuizAttemptDto> attempts = Collections.singletonList(testAttemptDto);
        when(quizService.getUserQuizAttempts(1L)).thenReturn(attempts);

        mockMvc.perform(get("/quizzes/1/attempts"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/attempts-list"))
                .andExpect(model().attributeExists("attempts"))
                .andExpect(model().attributeExists("quizId"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void showQuizResults_ReturnsResultsPage() throws Exception {
        when(quizService.getQuizAttemptResults(1L)).thenReturn(testAttemptDto);

        mockMvc.perform(get("/quizzes/1/results/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/results"))
                .andExpect(model().attributeExists("results"));
    }


} 