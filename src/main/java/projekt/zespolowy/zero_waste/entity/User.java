package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;

import java.util.Collection;
import java.util.List;

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

    @Column(nullable = false)
    private String password;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "total_points")
    private int totalPoints = 0;

    @Column(name = "average_rating", columnDefinition = "Double default 0")
    private Double averageRating;

    // Implementacja metod z interfejsu UserDetails
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;


    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    // Метод getAuthorities() для использования в Spring Security
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_ADMIN");  // Все пользователи имеют роль ADMIN
    }

    @Transient
    private int rank;

    public void setRank(int rank) {
        this.rank = rank;
    }

}

