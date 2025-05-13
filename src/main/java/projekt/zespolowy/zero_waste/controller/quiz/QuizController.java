package projekt.zespolowy.zero_waste.controller.quiz;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.dto.quiz.QuizDto;
import projekt.zespolowy.zero_waste.dto.quiz.QuizSubmissionDto;
import projekt.zespolowy.zero_waste.services.quiz.QuizService;

@Controller
@RequestMapping("/quizzes") // Base path for all quiz-related URLs
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // Display list of all quizzes
    @GetMapping
    public String listQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        return "quiz/list"; // Thymeleaf view: templates/quiz/list.html
    }

    // Show form to create a new quiz
    @GetMapping("/new")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quizDto", new QuizDto()); // Prepare empty DTO for the form
        // Add dummy question/answer for initial form structure if needed by Thymeleaf/JS
        // quizDto.getQuestions().add(new QuestionDto()); 
        // quizDto.getQuestions().get(0).getAnswers().add(new AnswerDto());
        // quizDto.getQuestions().get(0).getAnswers().add(new AnswerDto());
        return "quiz/create-form"; // Thymeleaf view: templates/quiz/create-form.html
    }

    // Process the creation of a new quiz
    @PostMapping
    public String createQuiz(@Valid @ModelAttribute("quizDto") QuizDto quizDto, 
                           BindingResult bindingResult, 
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // If validation errors, return to the form with error messages
            // Ensure the model attribute is repopulated for the view
            return "quiz/create-form"; 
        }
        try {
            quizService.createQuiz(quizDto);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz created successfully!");
            return "redirect:/quizzes"; // Redirect to the quiz list
        } catch (Exception e) {
            // Log the exception
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating quiz: " + e.getMessage());
            // Optionally return to form with specific error
            return "quiz/create-form"; 
        }
    }

    // Display a specific quiz for taking
    @GetMapping("/{id}")
    public String takeQuiz(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizDto quizDto = quizService.getQuizForTaking(id);
            model.addAttribute("quiz", quizDto);
            model.addAttribute("submissionDto", new QuizSubmissionDto()); // For the submission form
            return "quiz/take"; // Thymeleaf view: templates/quiz/take.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        }
    }

    // Process the submission of a quiz attempt
    @PostMapping("/{id}/submit")
    public String submitQuiz(@PathVariable Long id, 
                             @ModelAttribute QuizSubmissionDto submissionDto, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            // Log submission details to help debug
            System.out.println("Received quiz submission for quiz ID: " + id);
            
            // Check if answers map is null or empty
            if (submissionDto.getAnswers() == null || submissionDto.getAnswers().isEmpty()) {
                System.err.println("Error: No answers were submitted for quiz ID: " + id);
                redirectAttributes.addFlashAttribute("errorMessage", "Please answer at least one question before submitting.");
                return "redirect:/quizzes/" + id;
            }
            
            // Log which answers were submitted
            submissionDto.getAnswers().forEach((questionId, answerId) -> {
                System.out.println("Question ID: " + questionId + ", Selected Answer ID: " + answerId);
            });
            
            // Submit the quiz
            var resultDto = quizService.submitQuiz(id, submissionDto);
            
            // Redirect to the results page for this attempt
            return "redirect:/quizzes/" + id + "/results/" + resultDto.getAttemptId(); 
        } catch (EntityNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        } catch (IllegalStateException | SecurityException e) {
            System.err.println("Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quizzes";
        } catch (Exception e) {
            // Log exception
            System.err.println("Unexpected error submitting quiz: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while submitting the quiz: " + e.getMessage());
            return "redirect:/quizzes/" + id; // Redirect back to taking the quiz
        }
    }

    // Display the results of a specific quiz attempt
    @GetMapping("/{id}/results/{attemptId}")
    public String showQuizResults(@PathVariable Long id, @PathVariable Long attemptId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("results", quizService.getQuizAttemptResults(attemptId));
            return "quiz/results"; // Thymeleaf view: templates/quiz/results.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz attempt not found.");
            return "redirect:/quizzes/" + id + "/attempts";
        } catch (SecurityException e) {
             redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
             return "redirect:/quizzes";
        }
    }
    
    // Display list of attempts for a specific quiz by the current user
    @GetMapping("/{id}/attempts")
    public String listUserAttempts(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
         try {
            model.addAttribute("quizId", id); // Pass quizId to the view if needed
            model.addAttribute("attempts", quizService.getUserQuizAttempts(id));
            return "quiz/attempts-list"; // Thymeleaf view: templates/quiz/attempts-list.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quizzes";
        }
    }

    // Display leaderboard for a specific quiz
    @GetMapping("/{id}/leaderboard")
    public String showLeaderboard(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizDto quiz = quizService.getQuizForTaking(id); // Get quiz details
            model.addAttribute("quiz", quiz);
            model.addAttribute("topScores", quizService.getQuizLeaderboard(id));
            return "quiz/leaderboard";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        }
    }

    // Show form to edit an existing quiz
    @GetMapping("/{id}/edit")
    public String showEditQuizForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizDto quizDto = quizService.getQuizForEditing(id);
            model.addAttribute("quizDto", quizDto);
            model.addAttribute("quizId", id);
            return "quiz/edit-form"; // Thymeleaf view: templates/quiz/edit-form.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quizzes";
        }
    }

    // Process the update of an existing quiz
    @PostMapping("/{id}/edit")
    public String updateQuiz(@PathVariable Long id, 
                           @Valid @ModelAttribute("quizDto") QuizDto quizDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) { // Add Model to pass quizId back if validation fails
         if (bindingResult.hasErrors()) {
             model.addAttribute("quizId", id); // Need quizId for the form action URL
             return "quiz/edit-form";
        }
        try {
            quizService.updateQuiz(id, quizDto);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz updated successfully!");
            return "redirect:/quizzes";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
            return "redirect:/quizzes";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quizzes";
        } catch (Exception e) {
            // Log exception
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating quiz: " + e.getMessage());
            model.addAttribute("quizId", id);
            return "quiz/edit-form";
        }
    }
    
    // Process the deletion of a quiz
    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz deleted successfully!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Log exception
             redirectAttributes.addFlashAttribute("errorMessage", "Error deleting quiz.");
        }
        return "redirect:/quizzes";
    }

    // --- Exception Handling (Optional: Can use @ControllerAdvice) ---
    // Example: Handle specific exceptions locally if needed
    /*
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/quizzes";
    }

    @ExceptionHandler(SecurityException.class)
    public String handleSecurityException(SecurityException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/quizzes"; // Or redirect to an error page
    }
    */
} 