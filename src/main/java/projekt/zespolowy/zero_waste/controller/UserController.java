package projekt.zespolowy.zero_waste.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.dto.chat.UserChatDto;
import projekt.zespolowy.zero_waste.dto.user.UserPrivacyDto;
import projekt.zespolowy.zero_waste.dto.user.UserUpdateDto;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.PrivacySettings;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.security.Principal;

import static projekt.zespolowy.zero_waste.services.UserService.getUser;

@Controller
public class UserController {

    public static UserService userService = null;

    private final ReviewService reviewService;
    private final UserPreferenceService userPreferenceService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Konstruktorowe wstrzykiwanie zależności
    public UserController(UserService userService, ReviewService reviewService, UserPreferenceService userPreferenceService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.userPreferenceService = userPreferenceService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/accountDetails")
    public String accountDetails(Model model) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        // Pobierz recenzje użytkownika
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getId());

        // Przekaż dane do widoku
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new Review()); // Obiekt dla formularza

        return "User/accountDetails";
    }

    @GetMapping("/editAccount")
    public String editAccountForm(Model model) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername(user.getUsername());
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setPhoneNumber(user.getPhoneNumber());

        AuthProvider authProvider = user.getProvider();

        model.addAttribute("userUpdateDto", userUpdateDto);
        model.addAttribute("authProvider", authProvider.toString());
        return "User/editAccount";
    }

    @PostMapping("/editAccount")
    public String editAccount(@Valid @ModelAttribute("userUpdateDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "User/editAccount";
        }

        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();

        try {
            User updatedUser = userService.updateUser(userUpdateDto, username);
            refreshAuthentication(updatedUser, authentication);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            User userInner = customUser.getUser();
            AuthProvider authProvider = userInner.getProvider();
            model.addAttribute("authProvider", authProvider.toString());
            return "User/editAccount";
        }
        model.addAttribute("success", "Konto zaktualizowane pomyślnie");
        redirectAttributes.addFlashAttribute("success", "Konto zaktualizowane pomyślnie");
        return "redirect:/accountDetails";
    }

    @GetMapping("/editProfilePhoto")
    public String editProfilePhotoForm(Model model) {
        User user = getUser();

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setImageUrl(user.getImageUrl());

        AuthProvider authProvider = user.getProvider();

        model.addAttribute("userUpdateDto", userUpdateDto);
        model.addAttribute("authProvider", authProvider.toString());

        return "User/editProfilePhoto";
    }

    @PostMapping("/editProfilePhoto")
    public String editProfilePhoto(UserUpdateDto userUpdateDto, Model model, RedirectAttributes redirectAttributes) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();

        try {
            User updatedUser = userService.upadateUserPhoto(userUpdateDto, username);
            refreshAuthentication(updatedUser, authentication);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "User/editProfilePhoto";
        }

        model.addAttribute("success", "Zdjęcie profilowe zaktualizowane pomyślnie");
        redirectAttributes.addFlashAttribute("success", "Zdjęcie profilowe zaktualizowane pomyślnie");
        return "redirect:/accountDetails";
    }

    @GetMapping("/editPrivacySettings")
    public String editPrivacySettingsForm(Model model) {
        User user = findOrInitializePrivacySettings(getUser().getUsername());
        UserPrivacyDto userPrivacyDto = new UserPrivacyDto();
        userPrivacyDto.setPhoneVisible(user.getPrivacySettings().getPhoneVisible());
        userPrivacyDto.setEmailVisible(user.getPrivacySettings().getEmailVisible());
        userPrivacyDto.setSurnameVisible(user.getPrivacySettings().getSurnameVisible());

        AuthProvider authProvider = user.getProvider();

        model.addAttribute("privacyOptions", PrivacyOptions.values());
        model.addAttribute("userPrivacyDto", userPrivacyDto);
        model.addAttribute("authProvider", authProvider.toString());

        return "User/editPrivacySettings";
    }

    @PostMapping("/editPrivacySettings")
    public String editPrivacySettings(@Valid @ModelAttribute("userPrivacyDto") UserPrivacyDto userPrivacyDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "User/editPrivacySettings";
        }

        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();

        try {
            User updatedUser = userService.updatePrivacySettings(username, userPrivacyDto);
            refreshAuthentication(updatedUser, authentication);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "User/editPrivacySettings";
        }

        model.addAttribute("success", "Ustawienia prywatności zaktualizowane pomyślnie");
        redirectAttributes.addFlashAttribute("success", "Ustawienia prywatności zaktualizowane pomyślnie");
        return "redirect:/accountDetails";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteAccount")
    public String showDeleteAccountForm(Model model) {
        // model bez dodatkowych atrybutów, tylko formularz z polem "password"
        return "User/delete-account";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteAccount")
    public String handleDeleteAccount(@RequestParam("password") String password,
                                      RedirectAttributes redirectAttrs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        try {
            userService.deleteAccount(username, password);
            // wylogowanie po usunięciu
            SecurityContextHolder.clearContext();
            redirectAttrs.addFlashAttribute("info", "Twoje konto zostało usunięte.");
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            // błąd weryfikacji hasła lub brak usera
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/deleteAccount";
        }
    }

    private void refreshAuthentication(User updatedUser, Authentication aut) {
        CustomUser updatedCustomUser;
        if (aut instanceof OAuth2AuthenticationToken) {
            OidcUser oidcUser = (OidcUser) aut.getPrincipal();
            updatedCustomUser = new CustomUser(updatedUser, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        } else {
            updatedCustomUser = new CustomUser(updatedUser);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                updatedCustomUser,
                updatedCustomUser.getPassword(),
                updatedCustomUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/user/tasks")
    public String showUserTasks(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        List<UserTask> userTasks = userService.getUserTasksForUser(user);

        List<UserTask> completedTasks = userTasks.stream()
                .filter(UserTask::isCompleted)
                .collect(Collectors.toList());

        List<UserTask> incompleteTasks = userTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());

        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("incompleteTasks", incompleteTasks);

        return "Tasks/userTasks";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/likedArticles")
    public String showLikedArticles(Model model) {
        Set<ArticleDTO> likedArticles = userService.getLikedArticles();
        model.addAttribute("likedArticles", likedArticles);
        return "user/likedArticles";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/likedAdvices")
    public String showLikedAdvices(Model model) {
        Set<AdviceDTO> likedAdvices = userService.getLikedAdvices();
        model.addAttribute("likedAdvices", likedAdvices);
        return "user/likedAdvices";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/readArticles")
    public String showReadArticles(Model model) {
        Set<ArticleDTO> readArticles = userService.getReadArticles();
        model.addAttribute("readArticles", readArticles);
        return "user/readArticles";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/readAdvices")
    public String showReadAdvices(Model model) {
        Set<AdviceDTO> readAdvices = userService.getReadAdvices();
        model.addAttribute("readAdvices", readAdvices);
        return "user/readAdvices";
    }
    @GetMapping("/api/user/current")
    public ResponseEntity<UserChatDto> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = UserService.findByUsername(principal.getName());
        UserChatDto userDto = new UserChatDto(user.getId(), user.getUsername());
        return ResponseEntity.ok(userDto);
    }


    public User findOrInitializePrivacySettings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));
        PrivacySettings ps = user.getPrivacySettings();
        if (ps == null) {
            ps = new PrivacySettings();
            ps.setPhoneVisible(PrivacyOptions.PUBLIC);
            ps.setEmailVisible(PrivacyOptions.PUBLIC);
            ps.setSurnameVisible(PrivacyOptions.PUBLIC);
            ps.setUser(user);
            user.setPrivacySettings(ps);
            userRepository.save(user);
        }
        return user;
    }
}
