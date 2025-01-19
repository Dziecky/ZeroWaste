package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.services.EducationalServices.Article.ArticleService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final UserService userService;
    @Autowired
    public ArticleController(ArticleService articleService, ArticleMapper articleMapper, UserService userService) {
        this.articleService = articleService;
        this.articleMapper = articleMapper;
        this.userService = userService;
    }

    @GetMapping
    public String listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ArticleCategory category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tagName,
            Model model) {
            Pageable pageable = PageRequest.of(page, size);

        User currentUser = userService.getUser();

        Page<ArticleDTO> articleDTOPage = articleService.findArticlesWithLikes(category, title, tagName, pageable, currentUser);

            model.addAttribute("articlePage", articleDTOPage);
            //model.addAttribute("activePage", "articles");
            model.addAttribute("selectedCategory", category);
            model.addAttribute("categories", ArticleCategory.values());
            model.addAttribute("selectedTagName", tagName);
            model.addAttribute("title", title);
            return "Educational/Articles/articles";

    }
    // Show the form to create a new article
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("articleDTO", new ArticleDTO());
        model.addAttribute("categories", ArticleCategory.values());
        return "Educational/Articles/article_form";
    }

    // Save the new article
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public String createArticle(@ModelAttribute("articleDTO") ArticleDTO articleDTO) {
        articleService.createArticle(articleDTO);
        return "redirect:/articles";
    }
    // Show the form to edit an article
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Article> optionalArticle = articleService.getArticleById(id);
        if (optionalArticle.isPresent()) {
            ArticleDTO articleDTO = articleMapper.toDTO(optionalArticle.get());
            model.addAttribute("articleDTO", articleDTO);
            model.addAttribute("categories", ArticleCategory.values());
            return "Educational/Articles/article_form";
        } else {
            return "redirect:/articles";
        }
    }
    // Delete an article
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}") //do poprawny na DeleteMapping
    public String deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }
    // Update an article
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable("id") Long id, @ModelAttribute("articleDTO") ArticleDTO articleDTO) {
        articleService.updateArticle(id, articleDTO);
        return "redirect:/articles";
    }
    // View an article
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable("id") Long id, Model model) {
        Optional<Article> optionalArticle = articleService.getArticleById(id);
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            ArticleDTO articleDTO = articleMapper.toDTO(article);
            User currentUser = userService.getUser();
            articleDTO.setLikedByCurrentUser(article.getLikedByUsers().contains(currentUser));
            articleDTO.setLikesCount(article.getLikedByUsers().size());
            model.addAttribute("articleDTO", articleDTO);
            return "Educational/Articles/article_view";
        } else {
            return "redirect:/articles";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{id}")
    public String likeArticle(@PathVariable("id") Long id) {
        articleService.toggleLikeArticle(id);
        return "redirect:/articles";
    }
}
