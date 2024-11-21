package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.repository.ArticleRepository;

import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> getArticlesByCategory(ArticleCategory category, Pageable pageable) {
        if (category != null) {
            return articleRepository.findByArticleCategory(category, pageable);
        } else {
            return articleRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Article> getArticlesByTitle(String title, Pageable pageable) {
        return articleRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
