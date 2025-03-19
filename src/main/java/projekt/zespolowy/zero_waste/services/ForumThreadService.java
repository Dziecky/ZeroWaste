package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.repository.ForumThreadRepository;

import java.util.List;

@Service
public class ForumThreadService {
    private final ForumThreadRepository forumThreadRepository;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public ForumThreadService(ForumThreadRepository forumThreadRepository, UserService userService, CommentService commentService) {
        this.forumThreadRepository = forumThreadRepository;
        this.userService = userService;
        this.commentService = commentService;
    }
    public List<ForumThread> findAll() {
        return forumThreadRepository.findAll();
    }

    public void saveThread(ForumThread thread, User currentUser) {
        thread.setAuthor(currentUser);
        forumThreadRepository.save(thread);
    }

    public ForumThread findById(Long id) {
        return forumThreadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wątek o ID " + id + " nie został znaleziony"));
    }
    }
