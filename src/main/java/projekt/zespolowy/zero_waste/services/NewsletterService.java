package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;

@Service
public class NewsletterService {
    private final UserPreferenceService userPreferenceService;

    @Autowired
    public NewsletterService(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

}
