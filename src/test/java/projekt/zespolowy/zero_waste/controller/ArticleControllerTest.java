package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.services.EducationalServices.Article.ArticleService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArticleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ArticleController articleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
    }

    @Test
    void showCreateForm_shouldReturnArticleForm() throws Exception {
        mockMvc.perform(get("/articles/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("Educational/Articles/article_form"))
                .andExpect(model().attributeExists("articleDTO"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void createArticle_shouldRedirectToList() throws Exception {
        mockMvc.perform(post("/articles/save")
                        .param("title", "Test Article Title")
                        .param("content", "Test Article Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"));

        verify(articleService).createArticle(any(ArticleDTO.class));
    }

    @Test
    void showEditForm_shouldReturnFormWhenArticleExists() throws Exception {
        Article article = new Article();
        when(articleService.getArticleById(anyLong())).thenReturn(Optional.of(article));
        when(articleMapper.toDTO(any(Article.class))).thenReturn(new ArticleDTO());

        mockMvc.perform(get("/articles/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("Educational/Articles/article_form"))
                .andExpect(model().attributeExists("articleDTO"));
    }

    @Test
    void showEditForm_shouldRedirectWhenArticleNotFound() throws Exception {
        when(articleService.getArticleById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/articles/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"));
    }

    @Test
    void deleteArticle_shouldRedirectToList() throws Exception {
        mockMvc.perform(get("/articles/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"));

        verify(articleService).deleteArticle(1L);
    }

    @Test
    void likeArticle_shouldRedirectBack() throws Exception {
        mockMvc.perform(post("/articles/like/1")
                        .header("Referer", "/previous-article-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/previous-article-page"));

        verify(articleService).toggleLikeArticle(1L);
    }
}
