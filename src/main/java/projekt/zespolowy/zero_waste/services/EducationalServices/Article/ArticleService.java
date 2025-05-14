package projekt.zespolowy.zero_waste.services.EducationalServices.Article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.dto.ArticleCommentDTO;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleComment;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;
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
    Page<ArticleDTO> findArticlesWithLikesAndReads(ArticleCategory category, String title, String tagName, Pageable pageable, User currentUser);
    void toggleReadArticle(Long id);

    int getReads(Long id);

    List<ArticleCommentDTO> getComments(Long articleId);
    ArticleComment addComment(Long articleId, ArticleCommentDTO content);
    public void deleteComment(Long commentId, User currentUser);
    public void editComment(Long commentId, String newContent, User currentUser);
}
