package projekt.zespolowy.zero_waste.controller;

import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Wyświetlenie strony logowania/rejestracji
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Nazwa szablonu Thymeleaf
    }

    // Obsługa przesłania formularza rejestracji
    @PostMapping("/submitRegister")
    public String submitRegister(@ModelAttribute User user, Model model) {
        // Sprawdź, czy nazwa użytkownika lub email już istnieją
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Nazwa użytkownika już istnieje");
            return "login";
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email już istnieje");
            return "login";
        }
        // Zapisz użytkownika
        userService.registerUser(user);
        return "redirect:/login";
    }
}
