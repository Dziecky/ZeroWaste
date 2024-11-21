package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.ArticleCategory;
import projekt.zespolowy.zero_waste.services.ArticleService;
import projekt.zespolowy.zero_waste.services.ProductService;

import java.util.List;
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
            Model model) {

        Page<Article> articlePage = articleService.getArticlesByCategory(category, PageRequest.of(page, size));

        model.addAttribute("articlePage", articlePage);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", ArticleCategory.values());
        model.addAttribute("activePage", "articles");
        return "/Educational/articles";
    }
    // Show the form to create a new article
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", ArticleCategory.values());
        return "Educational/article_form";
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
            return "Educational/article_form";
        } else {
            return "redirect:/articles";
        }
    }
    // Delete an article
    @DeleteMapping("/delete/{id}")
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
            return "Educational/article_view";
        } else {
            return "redirect:/articles";
        }
    }


}
