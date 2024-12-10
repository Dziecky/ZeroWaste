package projekt.zespolowy.zero_waste.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;

@Controller
public class UserController {

    private final UserService userService;

    // Konstruktorowe wstrzykiwanie zależności
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accountDetails")
    public String accountDetails(Model model) {
        // Pobierz aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();
        model.addAttribute("user", user);
        return "accountDetails";
    }

    @GetMapping("/api/user/current")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Assuming your UserService fetches user details
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
}
