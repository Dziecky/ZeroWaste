package projekt.zespolowy.zero_waste.services.EducationalServices.Article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;

import java.util.Optional;

public interface ArticleService {
    Page<Article> getAllArticles(Pageable pageable);
    Article createArticle(ArticleDTO articleDTO);
    Article updateArticle(Long id, ArticleDTO articleDTO);
    Optional<Article> getArticleById(Long id);
    void deleteArticle(Long id);
    Page<Article> getArticlesByCategory(ArticleCategory category, Pageable pageable);
    Page<Article> getArticlesByTitle(String title, Pageable pageable);
    Page<Article> findByTags_NameIgnoreCase(String tagName, Pageable pageable);
    Page<Article> findArticles(ArticleCategory category, String title, String tagName, Pageable pageable);
    void toggleLikeArticle(Long id);
    int getLikes(Long id);
}
