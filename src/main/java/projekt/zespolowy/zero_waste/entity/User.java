package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;

import java.util.Collection;
import java.util.List;

import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Unikalna nazwa użytkownika

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password; // Zabezpieczone hasło

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "total_points")
    private int totalPoints;// BUSINESS lub NORMAL

    @Column(name = "average_rating", columnDefinition = "double default 0")
    private double averageRating;

    // Implementacja metod z interfejsu UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Dla uproszczenia, wszyscy użytkownicy są adminami podczas produkcji
        return List.of(() -> "ROLE_ADMIN");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Inne metody...
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @Override
    public boolean isEnabled() {
        return true;
    }
}
