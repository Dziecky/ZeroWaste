package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Challenge;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/newsletter")
public class UserPreferenceController {
    private final UserService userService;
    private final UserPreferenceService userPreferenceService;

    @Autowired
    public UserPreferenceController(UserService userService, UserPreferenceService userPreferenceService) {
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
    }

    // Wyświetla formularz ze starymi ustawieniami
    @GetMapping
    public String showNewsletterSettings(Model model) {
        User currentUser = userService.getUser(); // pobiera aktualnie zalogowanego
        // Pobierzemy preferencje z bazy lub stworzymy domyślne
        UserPreference preference = userPreferenceService
                .getUserPreference(currentUser)
                .orElseGet(() -> {
                    UserPreference p = new UserPreference();
                    p.setUser(currentUser);
                    p.setFrequency(Frequency.DAILY); // domyślne
                    p.setSubscribedTo(new HashSet<>());
                    return p;
                });

        model.addAttribute("userPreference", preference);
        return "/newsletter"; // nazwa widoku Thymeleaf: newsletter.html
    }

    // Odbiera dane z formularza i zapisuje w bazie
    @PostMapping
    public String updateNewsletterSettings(
            @RequestParam("frequency") Frequency frequency,
            @RequestParam(value = "subscribedTo", required = false) Set<SubscriptionType> subscribedTo // moze byc null
    ) {
        User currentUser = userService.getUser();

        userPreferenceService.saveUserPreference(currentUser, frequency, subscribedTo != null ? subscribedTo : Set.of());

        // Powrót do GET /newsletter
        return "redirect:/newsletter";
    }

    // Zwraca widok ze wszystkimi TIP-ami otrzymanymi przez usera
    @GetMapping("/tips")
    public String showUserTips(Model model) {
        User currentUser = userService.getUser();
        // Zakładamy, że user.getReceivedTips() trzyma TIP-y wysłane przez NotificationService
        List<Tip> tips = currentUser.getTips();

        model.addAttribute("tips", tips);
        return "/tips"; // np. inny widok do wyświetlenia listy TIP-ów
    }

    // Zwraca widok z CHALLENGE-ami otrzymanymi przez usera
    @GetMapping("/challenges")
    public String showUserChallenges(Model model) {
        User currentUser = userService.getUser();
        List<Challenge> challenges = currentUser.getChallenges();
        model.addAttribute("challenges", challenges);
        return "/challenges";
    }
}