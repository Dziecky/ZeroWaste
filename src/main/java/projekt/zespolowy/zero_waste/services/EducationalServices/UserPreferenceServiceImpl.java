package projekt.zespolowy.zero_waste.services.EducationalServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;
import projekt.zespolowy.zero_waste.repository.UserPreferenceRepository;

import java.util.Optional;
import java.util.Set;
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    @Autowired
    public UserPreferenceServiceImpl(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Override
    public Optional<UserPreference> getUserPreference(User user) {
        return userPreferenceRepository.findByUser(user);
    }

    @Override
    public UserPreference saveUserPreference(User user, Frequency frequency, Set<SubscriptionType> subscriptions) {
        Optional<UserPreference> existing = userPreferenceRepository.findByUser(user);
        UserPreference preference;
        if (existing.isPresent()) {
            preference = existing.get();
            preference.setFrequency(frequency);
            preference.setSubscribedTo(subscriptions);
        } else {
            preference = new UserPreference();
            preference.setUser(user);
            preference.setFrequency(frequency);
            preference.setSubscribedTo(subscriptions);
        }
        return userPreferenceRepository.save(preference);
    }
}
