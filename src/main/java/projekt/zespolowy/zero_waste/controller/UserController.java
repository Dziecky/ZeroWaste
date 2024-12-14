package projekt.zespolowy.zero_waste.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.dto.ReviewDto;
import projekt.zespolowy.zero_waste.dto.user.UserUpdateDto;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.Review;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.security.CustomUser;
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
    private final ArticleMapper articleMapper;

    // Konstruktorowe wstrzykiwanie zależności
    public UserController(UserService userService, ReviewService reviewService, ArticleMapper articleMapper) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.articleMapper = articleMapper;
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



    @GetMapping("/api/user/current")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = UserService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
}
