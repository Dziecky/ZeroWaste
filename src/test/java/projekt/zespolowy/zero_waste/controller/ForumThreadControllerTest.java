package projekt.zespolowy.zero_waste.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.mockito.junit.jupiter.MockitoExtension;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.Comment;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.services.CommentService;
import projekt.zespolowy.zero_waste.services.ForumThreadService;
import projekt.zespolowy.zero_waste.services.UserService;

@ExtendWith(MockitoExtension.class)
public class ForumThreadControllerTest {

    @InjectMocks
    private ForumThreadController forumThreadController;

    @Mock
    private ForumThreadService forumThreadService;

    private User user;

    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        model = new ExtendedModelMap();

    }

    @Test
    public void testListThreads() {
        List<ForumThread> threads = new ArrayList<>();
        threads.add(new ForumThread());
        when(forumThreadService.findAll()).thenReturn(threads);

        String view = forumThreadController.listThreads(model);

        assertEquals("forum/forum", view);
        assertTrue(model.containsAttribute("threads"));
        List<ForumThread> returnedThreads = (List<ForumThread>) model.getAttribute("threads");
        assertEquals(threads.size(), returnedThreads.size());
    }

    @Test
    public void testCreateThread() {
        String view = forumThreadController.createThread(model);

        assertEquals("forum/createThread", view);
        assertTrue(model.containsAttribute("thread"));
        ForumThread thread = (ForumThread) model.getAttribute("thread");
        assertNotNull(thread);
    }

    @Test
    public void testViewThread() {
        ForumThread thread = new ForumThread();
        thread.setId(1L);
        when(forumThreadService.findById(1L)).thenReturn(thread);

        String view = forumThreadController.viewThread(1L, model);

        assertEquals("forum/thread", view);
        assertTrue(model.containsAttribute("thread"));
        ForumThread returnedThread = (ForumThread) model.getAttribute("thread");
        assertEquals(1L, returnedThread.getId());
    }

    @Test
    void testViewThread_NoComments() {
        // Arrange
        long threadId = 2L;
        ForumThread threadWithoutComments = new ForumThread();
        threadWithoutComments.setId(threadId);
        threadWithoutComments.setTitle("Thread with no comments");
        threadWithoutComments.setComments(new ArrayList<>());

        when(forumThreadService.findById(threadId)).thenReturn(threadWithoutComments);

        // Act
        String viewName = forumThreadController.viewThread(threadId, model);

        // Assert
        assertEquals("forum/thread", viewName, "View name should be 'forum/thread'");
        assertTrue(model.containsAttribute("thread"), "Model should contain thread attribute");
        assertTrue(model.containsAttribute("newComment"), "Model should contain newComment attribute");

        Object threadAttribute = model.getAttribute("thread");
        assertNotNull(threadAttribute, "Thread attribute should not be null");
        assertInstanceOf(ForumThread.class, threadAttribute, "Thread attribute should be of type ForumThread");

        ForumThread returnedThread = (ForumThread) threadAttribute;
        assertEquals(threadId, returnedThread.getId(), "Returned thread ID mismatch");
        assertNotNull(returnedThread.getComments(), "Comments list in returned thread should not be null");
        assertTrue(returnedThread.getComments().isEmpty(), "Comments list in returned thread should be empty");

        verify(forumThreadService).findById(threadId); // Verify interaction
    }
    @Test
    void testViewThread_WithComments() {
        // Arrange
        long threadId = 3L;
        ForumThread threadWithComments = new ForumThread();
        threadWithComments.setId(threadId);
        threadWithComments.setTitle("Thread with comments");

        User commentAuthor = new User(); // Can reuse the 'user' from setup or create new
        commentAuthor.setId(5L);
        commentAuthor.setUsername("commenter");

        Comment comment1 = new Comment();
        comment1.setId(101L);
        comment1.setMessage("First comment");
        comment1.setAuthor(commentAuthor);


        Comment comment2 = new Comment();
        comment2.setId(102L);
        comment2.setMessage("Second comment");
        comment2.setAuthor(commentAuthor);

        List<Comment> comments = new ArrayList<>(Arrays.asList(comment1, comment2));
        threadWithComments.setComments(comments);

        when(forumThreadService.findById(threadId)).thenReturn(threadWithComments);

        // Act
        String viewName = forumThreadController.viewThread(threadId, model);

        // Assert
        assertEquals("forum/thread", viewName, "View name should be 'forum/thread'");
        assertTrue(model.containsAttribute("thread"), "Model should contain thread attribute");
        assertTrue(model.containsAttribute("newComment"), "Model should contain newComment attribute");

        Object threadAttribute = model.getAttribute("thread");
        assertNotNull(threadAttribute, "Thread attribute should not be null");
        assertInstanceOf(ForumThread.class, threadAttribute, "Thread attribute should be of type ForumThread");

        ForumThread returnedThread = (ForumThread) threadAttribute;
        assertEquals(threadId, returnedThread.getId(), "Returned thread ID mismatch");
        assertNotNull(returnedThread.getComments(), "Comments list in returned thread should not be null");
        assertFalse(returnedThread.getComments().isEmpty(), "Comments list in returned thread should not be empty");
        assertEquals(2, returnedThread.getComments().size(), "Comments list size should be 2");

        // Optionally check comment content if needed
        assertEquals("First comment", returnedThread.getComments().get(0).getMessage());
        assertEquals("Second comment", returnedThread.getComments().get(1).getMessage());

        verify(forumThreadService).findById(threadId); // Verify interaction
    }


}
