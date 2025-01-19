package projekt.zespolowy.zero_waste.services.EducationalServices;

import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;

import java.util.Optional;
import java.util.Set;

public interface UserPreferenceService {
    UserPreference saveUserPreference(User user, Frequency frequency, Set<SubscriptionType> subscriptions);
    Optional<UserPreference> getUserPreference(User user);
}
