package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.Comment;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }
    @Transactional
    public void saveComment(Comment comment, ForumThread thread, User author) {
        if (author == null) {
            throw new IllegalArgumentException("User cannot be null when saving a comment");
        }
        if (thread == null) {
            throw new IllegalArgumentException("Thread cannot be null when saving a comment");
        }
        comment.setAuthor(author);
        comment.setThread(thread);

        commentRepository.save(comment);
    }
    @Transactional
    public void editComment(Comment comment, String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        comment.setMessage(content);
        commentRepository.save(comment);
    }

}
