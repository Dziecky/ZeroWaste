package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;

@Controller
public class UserController {
    UserService userService;

    @GetMapping("/accountDetails")
    public String accountDetails(Model model, Principal principal) {
        // Pobierz aktualnie zalogowanego użytkownika
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "accountDetails";
    }
}