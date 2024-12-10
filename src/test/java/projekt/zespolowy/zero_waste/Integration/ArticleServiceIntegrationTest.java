package projekt.zespolowy.zero_waste.Integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.repository.ArticleRepository;
import projekt.zespolowy.zero_waste.services.ArticleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class ArticleServiceIntegrationTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;
    @Test
    void testGetArticlesByTitle_WithValidTitle() {
        // Arrange
        String title = "Integration Testing";
        Pageable pageable = PageRequest.of(0, 10);
        Article article = new Article();
        article.setTitle("Integration Testing in Spring Boot");
        article.setContent("Content about integration testing.");
        article.setArticleCategory(ArticleCategory.OTHER);
        articleRepository.save(article);

        // Act
        Page<Article> result = articleService.getArticlesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements(), "Should return one article");
        assertEquals("Integration Testing in Spring Boot", result.getContent().get(0).getTitle(), "Article title should match");
    }
}
