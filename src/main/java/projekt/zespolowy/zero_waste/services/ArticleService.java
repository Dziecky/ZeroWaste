package projekt.zespolowy.zero_waste.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.ArticleCategory;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    Page<Article> getAllArticles(Pageable pageable);
    Article saveArticle(Article article);
    Optional<Article> getArticleById(Long id);
    void deleteArticle(Long id);
    Page<Article> getArticlesByCategory(ArticleCategory category, Pageable pageable);
    Page<Article> getArticlesByTitle(String title, Pageable pageable);

}
