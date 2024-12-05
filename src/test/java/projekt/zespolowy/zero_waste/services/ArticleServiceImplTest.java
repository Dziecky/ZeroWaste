package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.repository.ArticleRepository;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.services.EducationalServices.Article.ArticleServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ArticleServiceImplTest {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository;

    public ArticleServiceImplTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetArticlesByTitle_WithValidTitle() {
        // Arrange
        String title = "Spring Boot Testing";
        Pageable pageable = PageRequest.of(0, 10);
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Spring Boot Testing");
        article.setContent("Content about Spring Boot Testing.");
        article.setArticleCategory(ArticleCategory.OTHER);
        List<Article> articles = Arrays.asList(article);
        Page<Article> articlePage = new PageImpl<>(articles, pageable, articles.size());

        when(articleRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(articlePage);

        // Act
        Page<Article> result = articleService.getArticlesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements(), "Should return one article");
        assertEquals("Spring Boot Testing", result.getContent().get(0).getTitle(), "Article title should match");
        verify(articleRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }
    @Test
    void testGetArticlesByTitle_WithNoMatchingArticles() {
        // Arrange
        String title = "Nonexistent Title";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Article> articlePage = new PageImpl<>(Arrays.asList());

        when(articleRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(articlePage);

        // Act
        Page<Article> result = articleService.getArticlesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero articles");
        verify(articleRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }
    @Test
    void testGetArticlesByTitle_WithNullTitle() {
        // Arrange
        String title = null;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Article> articlePage = new PageImpl<>(Arrays.asList());

        when(articleRepository.findAll(pageable)).thenReturn(articlePage);

        // Act
        Page<Article> result = articleService.getArticlesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero articles");
        verify(articleRepository, times(1)).findAll(pageable);
        verify(articleRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }
    @Test
    void testGetArticlesByTitle_WithEmptyTitle() {
        // Arrange
        String title = "   "; // Title with only spaces
        Pageable pageable = PageRequest.of(0, 10);
        Page<Article> articlePage = new PageImpl<>(Arrays.asList());

        when(articleRepository.findAll(pageable)).thenReturn(articlePage);

        // Act
        Page<Article> result = articleService.getArticlesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero articles");
        verify(articleRepository, times(1)).findAll(pageable);
        verify(articleRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }


}
