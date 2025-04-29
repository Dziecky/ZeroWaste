package projekt.zespolowy.zero_waste.services.quiz;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekt.zespolowy.zero_waste.dto.quiz.*;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.quiz.*;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.quiz.*;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository; // May not be used directly if cascade works
    private final AnswerRepository answerRepository;     // May not be used directly if cascade works
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubmittedAnswerRepository submittedAnswerRepository; // May not be used directly if cascade works
    private final UserRepository userRepository; // To fetch user entity for attempts/creation
    private final UserService userService; // To get current user

    // --- Quiz Creation and Management --- 

    @Transactional
    public Quiz createQuiz(QuizDto quizDto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User must be logged in to create a quiz.");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setCreator(currentUser);

        for (QuestionDto questionDto : quizDto.getQuestions()) {
            Question question = new Question();
            question.setText(questionDto.getText());
            Integer correctIdx = questionDto.getCorrectAnswerIndex(); // Get the submitted index

            // Iterate with index to check against correctIdx
            for (int aIdx = 0; aIdx < questionDto.getAnswers().size(); aIdx++) {
                AnswerDto answerDto = questionDto.getAnswers().get(aIdx);
                Answer answer = new Answer();
                answer.setText(answerDto.getText());
                // Set isCorrect based on the index submitted from the form
                answer.setCorrect(correctIdx != null && aIdx == correctIdx);
                question.addAnswer(answer); // Sets bidirectional relationship
            }
            // Add basic validation: Ensure at least one answer was marked correct if index was provided
            if (correctIdx != null && question.getAnswers().stream().noneMatch(Answer::isCorrect)){
                 // This indicates an issue, perhaps log a warning or adjust logic.
                 // For now, we proceed, assuming the form validation handles missing selection.
                 System.err.println("Warning: correctAnswerIndex was provided but no answer matched index " + correctIdx + " for question: " + question.getText());
            }
             else if (correctIdx == null && !questionDto.getAnswers().isEmpty()) {
                 // If no index submitted, maybe require one? Or ensure none are marked correct.
                 // Current logic sets all to false, which might be acceptable.
                 System.err.println("Warning: No correctAnswerIndex submitted for question: " + question.getText());
             }

            quiz.addQuestion(question); // Sets bidirectional relationship
        }

        return quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDto> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(quiz -> new QuizDto(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getCreator().getUsername()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizDto getQuizForTaking(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        // Map to DTO, ensuring isCorrect is false for answers
        return mapQuizToDto(quiz, false); 
    }
    
    @Transactional(readOnly = true)
    public QuizDto getQuizForEditing(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));
                
        // Check if the current user is the creator
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !quiz.getCreator().equals(currentUser)) {
            throw new SecurityException("You are not authorized to edit this quiz.");
        }

        // Map to DTO, including correct answer info
        return mapQuizToDto(quiz, true); 
    }

    @Transactional
    public Quiz updateQuiz(Long quizId, QuizDto quizDto) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        // Authorization check
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !existingQuiz.getCreator().equals(currentUser)) {
             throw new SecurityException("You are not authorized to update this quiz.");
        }

        // Update basic fields
        existingQuiz.setTitle(quizDto.getTitle());
        existingQuiz.setDescription(quizDto.getDescription());

        // --- More complex logic needed for updating questions/answers ---
        // Simplest approach (brute-force): remove old questions/answers, add new ones.
        // More sophisticated: identify changes, update/add/delete selectively.
        // For now, let's use the simpler approach (demonstration purposes)
        
        // Clear existing questions (cascade should handle answers)
        existingQuiz.getQuestions().clear(); 
        // This might require fetching the questions first if LAZY loading is strict
        // Or managing the relationship explicitly:
        // List<Question> oldQuestions = new ArrayList<>(existingQuiz.getQuestions());
        // oldQuestions.forEach(existingQuiz::removeQuestion);
        // questionRepository.deleteAll(oldQuestions); // If cascade doesn't work as expected

        // Add new/updated questions and answers from DTO
        for (QuestionDto questionDto : quizDto.getQuestions()) {
            Question question = new Question();
            question.setText(questionDto.getText());
            Integer correctIdx = questionDto.getCorrectAnswerIndex(); // Get the submitted index
            // Note: IDs from DTO are ignored here as we are recreating

            // Iterate with index to check against correctIdx
            for (int aIdx = 0; aIdx < questionDto.getAnswers().size(); aIdx++) {
                AnswerDto answerDto = questionDto.getAnswers().get(aIdx);
                Answer answer = new Answer();
                answer.setText(answerDto.getText());
                 // Set isCorrect based on the index submitted from the form
                answer.setCorrect(correctIdx != null && aIdx == correctIdx);
                question.addAnswer(answer); 
            }
            existingQuiz.addQuestion(question); 
        }

        return quizRepository.save(existingQuiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        // Authorization check
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !quiz.getCreator().equals(currentUser)) {
            throw new SecurityException("You are not authorized to delete this quiz.");
        }

        quizRepository.delete(quiz);
    }

    // --- Quiz Taking and Results --- 

    @Transactional
    public QuizAttemptDto submitQuiz(Long quizId, QuizSubmissionDto submissionDto) {
        // Input validation
        if (submissionDto == null || submissionDto.getAnswers() == null || submissionDto.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("No answers were provided in the submission");
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User must be logged in to submit a quiz.");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        // Fetch questions and their correct answers eagerly (or ensure they are loaded)
        // Using a Map for quick lookup might be efficient
        Map<Long, Question> questionsMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        
        // Basic validation to make sure the submitted answers belong to questions in this quiz
        for (Long questionId : submissionDto.getAnswers().keySet()) {
            if (!questionsMap.containsKey(questionId)) {
                throw new IllegalArgumentException("Submission contains question ID " + questionId 
                    + " which does not belong to quiz with ID " + quizId);
            }
        }
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(currentUser);
        attempt.setQuiz(quiz);
        attempt.setCompletedAt(LocalDateTime.now());

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
        
        System.out.println("Processing submissions for quiz: " + quiz.getTitle());

        // First save the attempt to get an ID
        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        
        for (Map.Entry<Long, Long> submissionEntry : submissionDto.getAnswers().entrySet()) {
            Long questionId = submissionEntry.getKey();
            Long selectedAnswerId = submissionEntry.getValue();
            
            System.out.println("Processing question ID: " + questionId + " - selected answer ID: " + selectedAnswerId);

            Question question = questionsMap.get(questionId);
            if (question == null) {
                System.err.println("Warning: Question not found for ID: " + questionId);
                continue; 
            }

            SubmittedAnswer submittedAnswer = new SubmittedAnswer();
            submittedAnswer.setQuestion(question);
            submittedAnswer.setQuizAttempt(savedAttempt);

            if (selectedAnswerId != null) {
                // Use a direct database query to fetch the selected Answer
                Optional<Answer> selectedAnswerOpt = answerRepository.findById(selectedAnswerId);

                if (selectedAnswerOpt.isPresent()) {
                    Answer selectedAnswer = selectedAnswerOpt.get();
                    
                    // Double-check that this answer belongs to our question
                    boolean answerBelongsToQuestion = false;
                    for (Answer answer : question.getAnswers()) {
                        if (answer.getId().equals(selectedAnswer.getId())) {
                            answerBelongsToQuestion = true;
                            break;
                        }
                    }
                    
                    if (!answerBelongsToQuestion) {
                        System.err.println("Warning: Answer ID " + selectedAnswerId + 
                            " doesn't belong to Question ID " + questionId);
                        // Skip this answer or handle as needed
                        continue;
                    }
                    
                    // Set the explicit relationship
                    submittedAnswer.setSelectedAnswer(selectedAnswer);
                    
                    // Save the submitted answer immediately to ensure it's persisted with relationships
                    SubmittedAnswer savedSubmittedAnswer = submittedAnswerRepository.save(submittedAnswer);
                    
                    if (savedSubmittedAnswer.getId() == null) {
                        System.err.println("Error: Failed to save submitted answer for question ID: " + questionId);
                    } else {
                        System.out.println("Successfully saved submitted answer with ID: " + savedSubmittedAnswer.getId());
                    }
                    
                    // Check for correct answer
                    if (selectedAnswer.isCorrect()) {
                        score++;
                        System.out.println("Correct answer for question ID: " + questionId);
                    } else {
                        System.out.println("Incorrect answer for question ID: " + questionId);
                    }
                } else {
                    System.err.println("Warning: Answer with ID " + selectedAnswerId + " not found in database");
                }
            } else {
                System.out.println("No answer selected for question ID: " + questionId);
                // Save even if no answer was selected
                submittedAnswerRepository.save(submittedAnswer);
            }
            
            // Add to attempt's collection 
            savedAttempt.getSubmittedAnswers().add(submittedAnswer);
        }

        // Update the score
        savedAttempt.setScore(score);
        System.out.println("Saving quiz attempt with score: " + score + "/" + totalQuestions);
        
        // Save again to update the score
        QuizAttempt finalAttempt = quizAttemptRepository.save(savedAttempt);
        
        // Return the fully populated DTO
        return mapAttemptToDto(finalAttempt);
    }
    
    @Transactional(readOnly = true)
    public QuizAttemptDto getQuizAttemptResults(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz Attempt not found with id: " + attemptId));
        
        // Authorization check (optional: allow users to see only their own attempts?)
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !attempt.getUser().equals(currentUser)) {
             // Decide on policy: throw error or maybe allow admins? For now, restrict.
             throw new SecurityException("You are not authorized to view this quiz attempt.");
        }
        
        return mapAttemptToDto(attempt);
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDto> getUserQuizAttempts(Long quizId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User must be logged in to view attempts.");
        }
        Quiz quiz = quizRepository.findById(quizId)
             .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        List<QuizAttempt> attempts = quizAttemptRepository.findByUserAndQuiz(currentUser, quiz);
        return attempts.stream()
                .map(this::mapAttemptToDtoWithoutDetails) // Use a simpler mapping for lists
                .collect(Collectors.toList());
    }

    // --- Helper Mappers --- 

    private QuizDto mapQuizToDto(Quiz quiz, boolean includeCorrectness) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCreatorUsername(quiz.getCreator() != null ? quiz.getCreator().getUsername() : "N/A");

        dto.setQuestions(quiz.getQuestions().stream()
                .map(q -> mapQuestionToDto(q, includeCorrectness))
                .collect(Collectors.toList()));
        return dto;
    }

    private QuestionDto mapQuestionToDto(Question question, boolean includeCorrectness) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setAnswers(question.getAnswers().stream()
                .map(a -> mapAnswerToDto(a, includeCorrectness))
                .collect(Collectors.toList()));
        return dto;
    }

    private AnswerDto mapAnswerToDto(Answer answer, boolean includeCorrectness) {
        AnswerDto dto = new AnswerDto();
        dto.setId(answer.getId());
        dto.setText(answer.getText());
        if (includeCorrectness) {
            dto.setCorrect(answer.isCorrect());
        } else {
            dto.setCorrect(false); // Never reveal correctness when user is taking quiz
        }
        return dto;
    }
    
    private QuizAttemptDto mapAttemptToDto(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz(); // Assumes Quiz is fetched or accessible
        int totalQuestions = quiz != null ? quiz.getQuestions().size() : 0; // Handle potential null quiz?
        
        List<SubmittedAnswerDto> results = attempt.getSubmittedAnswers().stream()
            .map(this::mapSubmittedAnswerToDto)
            .collect(Collectors.toList());

        return new QuizAttemptDto(
            attempt.getId(),
            quiz != null ? quiz.getId() : null,
            quiz != null ? quiz.getTitle() : "Quiz Deleted",
            attempt.getUser().getUsername(),
            attempt.getScore(),
            totalQuestions, 
            attempt.getCompletedAt(),
            results
        );
    }

    private SubmittedAnswerDto mapSubmittedAnswerToDto(SubmittedAnswer submitted) {
        Question question = submitted.getQuestion();
        Answer selectedAnswer = submitted.getSelectedAnswer();
        Answer correctAnswer = question.getAnswers().stream()
            .filter(Answer::isCorrect)
            .findFirst()
            .orElse(null);
        
        // Debug output to track issues
        if (correctAnswer == null) {
            System.err.println("Warning: No correct answer found for question ID: " + question.getId());
        }
        
        if (selectedAnswer == null) {
            System.err.println("Warning: No selected answer found for submitted answer on question ID: " + question.getId());
        }
        
        // In cases where the question has no correct answer marked, we'll treat the selected answer as correct
        // This is a fallback in case of data issues
        boolean isCorrect = (selectedAnswer != null && correctAnswer != null && selectedAnswer.getId().equals(correctAnswer.getId()));

        return new SubmittedAnswerDto(
            question.getId(),
            question.getText(),
            selectedAnswer != null ? selectedAnswer.getId() : null,
            selectedAnswer != null ? selectedAnswer.getText() : "No answer",
            correctAnswer != null ? correctAnswer.getId() : null,
            correctAnswer != null ? correctAnswer.getText() : "No correct answer defined",
            isCorrect
        );
    }

    // Simpler DTO mapping for lists of attempts (avoids fetching full details)
    private QuizAttemptDto mapAttemptToDtoWithoutDetails(QuizAttempt attempt) {
         Quiz quiz = attempt.getQuiz();
         int totalQuestions = quiz != null ? quiz.getQuestions().size() : 0;
         return new QuizAttemptDto(
            attempt.getId(),
            quiz != null ? quiz.getId() : null,
            quiz != null ? quiz.getTitle() : "Quiz Deleted",
            attempt.getUser().getUsername(),
            attempt.getScore(),
            totalQuestions, 
            attempt.getCompletedAt(),
            null // No detailed results in list view
        );
    }
} 