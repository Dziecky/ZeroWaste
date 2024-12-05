package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.services.EducationalServices.Article.ArticleService;

import java.util.Optional;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ArticleCategory category,
            @RequestParam(required = false) String title,
            Model model) {
        Page<Article> articlePage;
        if (title != null && !title.trim().isEmpty()) {
            // If title is provided, search by title
            articlePage = articleService.getArticlesByTitle(title, PageRequest.of(page, size));
            model.addAttribute("searchMode", "title");
        } else if (category != null) {
            // If category is provided, filter by category
            articlePage = articleService.getArticlesByCategory(category, PageRequest.of(page, size));
            model.addAttribute("searchMode", "category");
        } else {
            // If no filters, display all articles
            articlePage = articleService.getAllArticles(PageRequest.of(page, size));
            model.addAttribute("searchMode", "none");
        }
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", ArticleCategory.values());
        model.addAttribute("activePage", "articles");
        return "/Educational/Articles/articles";
    }
    // Show the form to create a new article
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", ArticleCategory.values());
        return "Educational/Articles/article_form";
    }

    // Save the new article
    @PostMapping("/save")
    public String saveArticle(@ModelAttribute("article") Article article) {
        articleService.saveArticle(article);
        return "redirect:/articles";
    }
    // Show the form to edit an article
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Article> optionalArticle = articleService.getArticleById(id);
        if (optionalArticle.isPresent()) {
            model.addAttribute("article", optionalArticle.get());
            model.addAttribute("categories", ArticleCategory.values());
            return "Educational/Articles/article_form";
        } else {
            return "redirect:/articles";
        }
    }
    // Delete an article
    @GetMapping("/delete/{id}") //do poprawny na DeleteMapping
    public String deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }
    // View an article
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable("id") Long id, Model model) {
        Optional<Article> optionalArticle = articleService.getArticleById(id);
        if (optionalArticle.isPresent()) {
            model.addAttribute("article", optionalArticle.get());
            return "Educational/Articles/article_view";
        } else {
            return "redirect:/articles";
        }
    }


}
