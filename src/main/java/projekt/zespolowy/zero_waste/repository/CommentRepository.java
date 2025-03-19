package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor(User author);
    List<Comment> findAllByThreadId(Long threadId);

}

