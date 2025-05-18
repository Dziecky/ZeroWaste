package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleComment;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}
