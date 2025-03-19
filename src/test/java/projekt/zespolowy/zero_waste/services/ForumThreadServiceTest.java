package projekt.zespolowy.zero_waste.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.repository.ForumThreadRepository;

@ExtendWith(MockitoExtension.class)
public class ForumThreadServiceTest {

    @Mock
    private ForumThreadRepository forumThreadRepository;

    // Przyjmujemy, że inne zależności nie wpływają na logikę metod, więc możemy je pominąć lub mockować:
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private ForumThreadService forumThreadService;

    @Test
    public void testFindAll() {
        List<ForumThread> threads = new ArrayList<>();
        threads.add(new ForumThread());
        when(forumThreadRepository.findAll()).thenReturn(threads);

        List<ForumThread> result = forumThreadService.findAll();
        assertEquals(threads.size(), result.size());
        verify(forumThreadRepository, times(1)).findAll();
    }

    @Test
    public void testSaveThread() {
        ForumThread thread = new ForumThread();
        User user = new User();
        forumThreadService.saveThread(thread, user);

        assertEquals(user, thread.getAuthor());
        verify(forumThreadRepository, times(1)).save(thread);
    }

    @Test
    public void testFindByIdFound() {
        ForumThread thread = new ForumThread();
        thread.setId(1L);
        when(forumThreadRepository.findById(1L)).thenReturn(Optional.of(thread));

        ForumThread result = forumThreadService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(forumThreadRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            forumThreadService.findById(1L);
        });
        assertTrue(exception.getMessage().contains("nie został znaleziony"));
    }
}
