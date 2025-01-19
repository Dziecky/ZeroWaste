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
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/preferences")
public class UserPreferenceController {
    private final UserService userService;
    private final UserPreferenceService userPreferenceService;

    @Autowired
    public UserPreferenceController(UserService userService, UserPreferenceService userPreferenceService) {
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showPreferencesForm(Model model) {
        User currentUser = userService.getUser();
        UserPreference preference = userPreferenceService.getUserPreference(currentUser).orElse(new UserPreference());

        model.addAttribute("frequencyOptions", Frequency.values());
        model.addAttribute("subscriptionOptions", SubscriptionType.values());
        model.addAttribute("userPreference", preference);

        return "preferences/preferences_form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String updatePreferences(@RequestParam("frequency") Frequency frequency,
                                    @RequestParam(value = "subscriptions", required = false) SubscriptionType[] subscriptions) {
        User currentUser = userService.getUser();
        Set<SubscriptionType> subscriptionSet = subscriptions != null ? new HashSet<>(Arrays.asList(subscriptions)) : new HashSet<>();
        userPreferenceService.saveUserPreference(currentUser, frequency, subscriptionSet);
        return "redirect:/preferences";
    }
}
