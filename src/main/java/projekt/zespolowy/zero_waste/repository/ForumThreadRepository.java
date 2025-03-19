package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    // Znajdź wątki o określonym typie
    //List<Thread> findByThreadType(ThreadType threadType);-
    List<ForumThread> findAll();
}
