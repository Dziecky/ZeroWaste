package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Challenge;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;
import projekt.zespolowy.zero_waste.entity.forum.Comment;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;

import projekt.zespolowy.zero_waste.entity.enums.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"reviews"}) // Exclude the reviews field from toString()
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Unikalna nazwa użytkownika

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column()
    private String password; // Zabezpieczone hasło

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "total_points")
    private int totalPoints; // BUSINESS lub NORMAL

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    @Column
    private String imageUrl;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Advice> advices;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles;
    @Transient
    private int rank;
    @Column(name = "average_rating", columnDefinition = "Double default 0")
    private double averageRating;
    @ManyToMany
    @JoinTable(name = "article_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id"))
    private List<Article> likedArticles;
    @ManyToMany
    @JoinTable(name = "advice_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "advice_id"))
    private List<Advice> likedAdvices;
    @ManyToMany
    @JoinTable(name = "article_read",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id"))
    private List<Article> readArticles;
    @ManyToMany
    @JoinTable(name = "advice_read",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "advice_id"))
    private List<Advice> readAdvices;
    @ManyToMany
    @JoinTable(name = "user_tips",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tip_id"))
    private List<Tip> tips;
    @ManyToMany
    @JoinTable(name = "user_challenges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "challenge_id"))
    private List<Challenge> challenges;
    @ManyToMany
    @JoinTable(name = "announcement_views",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "announcement_id"))
    private Set<Announcement> viewedAnnouncements = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "announcement_upvotes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "announcement_id"))
    private Set<Announcement> upvotedAnnouncements = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "announcement_downvotes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "announcement_id"))
    private Set<Announcement> downvotedAnnouncements = new HashSet<>();
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PrivacySettings privacySettings;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumThread> threads;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    // Metoda getAuthorities() do użycia w CustomUser
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name());
    }
}



