package projekt.zespolowy.zero_waste.entity.EducationalEntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;

import java.util.Set;

@Entity
@Table(name = "user_preference")
@Getter
@Setter
public class UserPreference {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @ElementCollection(targetClass = SubscriptionType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_subscription_types", joinColumns = @JoinColumn(name = "user_preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private Set<SubscriptionType> subscribedTo;
}
