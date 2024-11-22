package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // Metoda getAuthorities() do użycia w CustomUser
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Dla uproszczenia, wszyscy użytkownicy mają rolę ADMIN podczas produkcji
        return List.of(() -> "ROLE_ADMIN");
    }
}
