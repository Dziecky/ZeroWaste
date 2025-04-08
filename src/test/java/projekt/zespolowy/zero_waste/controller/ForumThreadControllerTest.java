package projekt.zespolowy.zero_waste.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    private Model model;

    @BeforeEach
    void setUp() {
        // Używamy ExtendedModelMap jako implementacji Model
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
    public void testAddComment() {
        ForumThread thread = new ForumThread();
        thread.setId(1L);
        when(forumThreadService.findById(1L)).thenReturn(thread);
        User user = userService.getUserTest();
        when(userService.getUser()).thenReturn(user);
        Comment newComment = new Comment();
        String view = forumThreadController.addComment(1L, newComment, model);

        assertEquals("redirect:/forum/thread/1", view);
        verify(forumThreadService).findById(1L);
        verify(commentService).saveComment(newComment, thread, userService.getUser());
    }
}
