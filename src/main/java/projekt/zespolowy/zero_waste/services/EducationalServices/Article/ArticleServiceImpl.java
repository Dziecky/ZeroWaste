package projekt.zespolowy.zero_waste.services.EducationalServices.Article;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.repository.ArticleRepository;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;
import projekt.zespolowy.zero_waste.services.ActivityLogService;
import projekt.zespolowy.zero_waste.services.TagService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ActivityLogService logService;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final TagService tagService;

    private final UserService userService;

    private final UserTaskRepository userTaskRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper, TagService tagService, UserService userService, UserTaskRepository userTaskRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.tagService = tagService;
        this.userService = userService;
        this.userTaskRepository = userTaskRepository;

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Article createArticle(ArticleDTO articleDTO) {
        Article article = articleMapper.toEntity(articleDTO, tagService);
        article.setAuthor(UserService.getUser());

        logService.log(UserService.getUser().getId(), ActivityType.ARTICLE_CREATED, article.getId(), Map.of("title", article.getTitle(), "category", article.getArticleCategory().toString()));

        Task createReviewTask = taskRepository.findByTaskName("Dodaj artykuł");

        if (createReviewTask != null) {
            // Pobierz zadanie użytkownika
            UserTask userTask = userTaskRepository.findByUserAndTask(UserService.getUser(), createReviewTask);
            // Zwiększ postęp zadania
            userTask.setProgress(userTask.getProgress() + 1);

            // Sprawdź, czy zadanie zostało ukończone
            if (userTask.getProgress() >= createReviewTask.getRequiredActions()) {
                userTask.setCompleted(true);
                userTask.setCompletionDate(LocalDate.now());

                logService.log(UserService.getUser().getId(), ActivityType.TASK_COMPLETED, userTask.getTask().getId(), Map.of("task name", createReviewTask.getTask_name(), "point awarded", createReviewTask.getPointsAwarded()));

                UserService.getUser().setTotalPoints(UserService.getUser().getTotalPoints() + createReviewTask.getPointsAwarded());
                userRepository.save(UserService.getUser()); // Zapisz zmiany w użytkowniku
            }

            userTaskRepository.save(userTask);
        }

        return articleRepository.save(article);
    }
    @Override
    @Transactional
    public Article updateArticle(Long id, ArticleDTO articleDTO) {
        return articleRepository.findById(id).map(existingArticle -> {
            articleMapper.updateArticleFromDTO(articleDTO, existingArticle, tagService);
            return articleRepository.save(existingArticle);
        }).orElseThrow(() -> new RuntimeException("Article not found with id " + id));
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
        if(title != null && !title.trim().isEmpty()){
            return articleRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            return articleRepository.findAll(pageable);
        }
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public Page<Article> findByTags_NameIgnoreCase(String tagName, Pageable pageable) {
        return articleRepository.findByTags_NameIgnoreCase(tagName, pageable);
    }

    @Override
    public Page<Article> findArticles(ArticleCategory category, String title, String tagName, Pageable pageable) {
        Specification<Article> specification = Specification.where(null);
        if (category != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("articleCategory"), category));
        }
        if (title != null && !title.trim().isEmpty()) {
            specification = specification.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (tagName != null && !tagName.trim().isEmpty()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.join("tags").get("name"), tagName));
        }
        return articleRepository.findAll(specification, pageable);
    }
    @Transactional
    public void saveArticle(ArticleDTO articleDTO) {
        Article article = articleMapper.toEntity(articleDTO, tagService);
        article.setAuthor(userService.getUser());
        articleRepository.save(article);
    }

    @Override
    public int getLikes(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));
        return article.getLikedByUsers().size();
    }

    @Override
    @Transactional
    public void toggleLikeArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));

        User currentUser = userService.getUser();

        if(article.getLikedByUsers().contains(currentUser)) {
            article.getLikedByUsers().remove(currentUser);
            currentUser.getLikedArticles().remove(article);
        } else {
            article.getLikedByUsers().add(currentUser);
            currentUser.getLikedArticles().add(article);
        }

        articleRepository.save(article);
        userService.save(currentUser);
    }
    @Override //to be refactored
    public Page<ArticleDTO> findArticlesWithLikesAndReads(ArticleCategory category, String title, String tagName, Pageable pageable, User currentUser) {
        Page<Article> articles = findArticles(category, title, tagName, pageable);
        return articles.map(article -> {
            ArticleDTO dto = articleMapper.toDTO(article);
            dto.setLikedByCurrentUser(article.getLikedByUsers().contains(currentUser));
            dto.setLikesCount(article.getLikedByUsers().size());
            dto.setReadByCurrentUser(article.getReadByUsers().contains(currentUser));
            dto.setReadsCount(article.getReadByUsers().size());
            return dto;
        });
    }

    @Override
    @Transactional
    public void toggleReadArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));

        User currentUser = userService.getUser();
        if(article.getReadByUsers().contains(currentUser)) {
            return;
        } else {
            article.getReadByUsers().add(currentUser);
            currentUser.getReadArticles().add(article);
        }
        articleRepository.save(article);
        userService.save(currentUser);
    }

    @Override
    public int getReads(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));
        return article.getReadByUsers().size();
    }


}
