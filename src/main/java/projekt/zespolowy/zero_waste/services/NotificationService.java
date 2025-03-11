package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Challenge;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;
import projekt.zespolowy.zero_waste.repository.ChallengeRepository;
import projekt.zespolowy.zero_waste.repository.TipRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class NotificationService {

    private final UserService userService;
    private final UserPreferenceService userPreferenceService;
    private final TipRepository tipRepository;
    private final ChallengeRepository challengeRepository;

    @Autowired
    public NotificationService(UserService userService, UserPreferenceService userPreferenceService, TipRepository tipRepository, ChallengeRepository challengeRepository) {
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
        this.tipRepository = tipRepository;
        this.challengeRepository = challengeRepository;
    }
    LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    @Scheduled(cron = "0 0 8 * * *")
    public void sendNotification() {
        LocalDate today = getCurrentDate();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfMonth = today.getDayOfMonth();

        List<User> users = userService.getAllUsers();
        for (User user : users) {
            userPreferenceService.getUserPreference(user).ifPresent(preferences -> {
                Frequency frequency = preferences.getFrequency();
                Set<SubscriptionType> subscribedTo = preferences.getSubscribedTo();
                if (shouldSentNotificationToday(frequency, dayOfWeek, dayOfMonth)) {
                    if (subscribedTo.contains(SubscriptionType.TIP)) {
                        Tip tip = getRandomTip();
                        if (tip != null) {
                            user.getTips().add(tip);
                            userService.save(user);
                        }
                        if (subscribedTo.contains(SubscriptionType.CHALLENGE)) {
                            Challenge challenge = getRandomChallenge();
                            if (challenge != null) {
                                user.getChallenges().add(challenge);
                                userService.save(user);
                            }
                        }
                    }
                }
            });
        }

    }

    private Challenge getRandomChallenge() {
        return challengeRepository.findRandomChallenge();
    }

    private Tip getRandomTip() {
        return tipRepository.findRandomTip();


    }

    private boolean shouldSentNotificationToday(Frequency frequency, DayOfWeek dayOfWeek, int dayOfMonth) {
        switch (frequency) {
            case DAILY:
                return true;
            case WEEKLY:
                return dayOfWeek == DayOfWeek.MONDAY;
            case MONTHLY:
                return dayOfMonth == 1;
            default:
                return false;
        }
    }
}
