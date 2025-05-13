package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.entity.enums.VoteType;
import projekt.zespolowy.zero_waste.repository.*;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.mapper.ReviewMapper;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private User targetUser;
    private Review testReview;
    private Task testTask;
    private UserTask testUserTask;
    private CustomUser customUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setTotalPoints(0);

        targetUser = new User();
        targetUser.setId(2L);

        testReview = new Review();
        testReview.setId(1L);
        testReview.setContent("Test content");
        testReview.setRating(5);
        testReview.setCreatedDate(LocalDate.now().atStartOfDay());
        testReview.setUser(testUser);
        testReview.setTargetUserId(targetUser.getId());

        testTask = new Task();
        testTask.setId(1L);
        testTask.setRequiredActions(1);
        testTask.setPointsAwarded(10);

        testUserTask = new UserTask();
        testUserTask.setUser(testUser);
        testUserTask.setTask(testTask);
        testUserTask.setProgress(0);
        testUserTask.setCompleted(false);

        customUser = new CustomUser(testUser);
    }

    @Test
    void createReview_ShouldSaveReviewAndUpdateUserTask() {
        // Arrange
        when(taskRepository.findByTaskName("Dodaj pierwszą recenzję na profilu użytkownika")).thenReturn(testTask);
        when(userTaskRepository.findByUserAndTask(testUser, testTask)).thenReturn(testUserTask);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Review result = reviewService.createReview(testReview);

        // Assert
        assertNotNull(result);
        assertEquals(testReview.getContent(), result.getContent());
        verify(userTaskRepository).save(testUserTask);
        assertEquals(1, testUserTask.getProgress());
        assertTrue(testUserTask.isCompleted());
        assertEquals(10, testUser.getTotalPoints());
        verify(userRepository).save(testUser);
    }

    @Test
    void createResponse_ShouldSaveResponseReview() {
        // Arrange
        Review parentReview = new Review();
        parentReview.setId(2L);
        testReview.setParentReview(parentReview);
        testReview.setRating(0);

        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Review result = reviewService.createResponse(testReview);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getRating());
        assertEquals(parentReview, result.getParentReview());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldUpdateExistingReview() {
        // Arrange
        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setContent("Updated content");
        updatedReview.setRating(4);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        // Act
        Review result = reviewService.updateReview(updatedReview);

        // Assert
        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        assertEquals(4, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldDeleteReviewAndChildren() {
        // Arrange
        Review childReview = new Review();
        childReview.setId(2L);
        List<Review> childReviews = Collections.singletonList(childReview);

        when(reviewRepository.findByParentReview(testReview)).thenReturn(childReviews);

        // Act
        reviewService.deleteReview(testReview);

        // Assert
        verify(reviewRepository).delete(childReview);
        verify(reviewRepository).delete(testReview);
    }



    @Test
    void vote_ShouldAddNewUpvote() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.findByUserAndReview(testUser, testReview)).thenReturn(Optional.empty());

        // Act
        reviewService.vote(1L, 1L, true);

        // Assert
        verify(voteRepository).save(any(Vote.class));
        assertEquals(1, testReview.getVotes());
        verify(reviewRepository).save(testReview);
    }

    @Test
    void vote_ShouldChangeFromDownvoteToUpvote() {
        // Arrange
        Vote existingVote = new Vote();
        existingVote.setVoteType(VoteType.DOWN);
        testReview.setVotes(-1);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.findByUserAndReview(testUser, testReview)).thenReturn(Optional.of(existingVote));

        // Act
        reviewService.vote(1L, 1L, true);

        // Assert
        assertEquals(VoteType.UP, existingVote.getVoteType());
        assertEquals(1, testReview.getVotes());
        verify(voteRepository).save(existingVote);
        verify(reviewRepository).save(testReview);
    }

    @Test
    void vote_ShouldRemoveExistingUpvote() {
        // Arrange
        Vote existingVote = new Vote();
        existingVote.setVoteType(VoteType.UP);
        testReview.setVotes(1);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.findByUserAndReview(testUser, testReview)).thenReturn(Optional.of(existingVote));

        // Act
        reviewService.vote(1L, 1L, true);

        // Assert
        verify(voteRepository).delete(existingVote);
        assertEquals(0, testReview.getVotes());
        verify(reviewRepository).save(testReview);
    }

    @Test
    void getReviewById_ShouldReturnReview() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act
        Review result = reviewService.getReviewById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReview, result);
    }

    @Test
    void getReviewById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reviewService.getReviewById(1L));
    }
}
