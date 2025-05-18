package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import projekt.zespolowy.zero_waste.dto.ArticleCommentDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleComment;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.ArticleCommentRepository;
import projekt.zespolowy.zero_waste.repository.ArticleRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.Article.ArticleServiceImpl;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceImplCommentTest {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository commentRepository;

    @Mock
    private UserService userService;

    private Article article;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        article = new Article();
        article.setId(1L);
        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
    }

    @Test
    void testGetComments_ReturnsList() {
        // Arrange
        ArticleComment comment = new ArticleComment();
        comment.setId(10L);
        comment.setContent("Test comment");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setArticle(article);

        when(commentRepository.findByArticleIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(comment));

        // Act
        List<ArticleCommentDTO> result = articleService.getComments(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test comment", result.get(0).getContent());
        assertEquals("john_doe", result.get(0).getAuthorUsername());
    }

    @Test
    void testDeleteComment_OwnComment_Success() {
        // Arrange
        ArticleComment comment = new ArticleComment();
        comment.setId(5L);
        comment.setAuthor(user);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        // Act
        articleService.deleteComment(5L, user);

        // Assert
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void testDeleteComment_NotAuthor_ThrowsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);

        ArticleComment comment = new ArticleComment();
        comment.setId(5L);
        comment.setAuthor(otherUser);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        // Assert + Act
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> articleService.deleteComment(5L, user));

        assertEquals("You are not allowed to delete this comment", ex.getMessage());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void testEditComment_OwnComment_Success() {
        // Arrange
        ArticleComment comment = new ArticleComment();
        comment.setId(7L);
        comment.setAuthor(user);
        comment.setContent("Old content");

        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment));

        // Act
        articleService.editComment(7L, "Updated content", user);

        // Assert
        assertEquals("Updated content", comment.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testEditComment_NotAuthor_ThrowsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(99L);

        ArticleComment comment = new ArticleComment();
        comment.setId(8L);
        comment.setAuthor(otherUser);

        when(commentRepository.findById(8L)).thenReturn(Optional.of(comment));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> articleService.editComment(8L, "Hacked", user));
        assertEquals("You are not allowed to edit this comment", ex.getMessage());
        verify(commentRepository, never()).save(any());
    }
}
