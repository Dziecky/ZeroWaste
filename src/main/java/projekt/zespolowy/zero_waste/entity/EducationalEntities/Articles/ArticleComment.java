package projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles;

import jakarta.persistence.*;
import lombok.*;
import projekt.zespolowy.zero_waste.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
