package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.Data;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;

@Entity
@Table(name = "privacy_settings")
@Data
public class PrivacySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PrivacyOptions phoneVisible;
    @Enumerated(EnumType.STRING)
    private PrivacyOptions emailVisible;
    @Enumerated(EnumType.STRING)
    private PrivacyOptions surnameVisible;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
