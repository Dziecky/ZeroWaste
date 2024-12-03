package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import projekt.zespolowy.zero_waste.dto.UserRegistrationDto;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Użytkownik jest zalogowany, przekieruj na stronę główną lub inną
            return "redirect:/";
        }
        return "login"; // Nazwa szablonu Thymeleaf
    }

    // Obsługa przesłania formularza rejestracji
    @PostMapping("/submitRegister")
    public String submitRegister(@ModelAttribute("user") UserRegistrationDto userDto, Model model) {
        // Sprawdź, czy nazwa użytkownika lub email już istnieją
        if (userService.findByUsername(userDto.getUsername()) != null) {
            model.addAttribute("error", "Nazwa użytkownika już istnieje");
            return "login";
        }
        if (userService.findByEmail(userDto.getEmail()) != null) {
            model.addAttribute("error", "Email już istnieje");
            return "login";
        }
        // Zapisz użytkownika
        userService.registerUser(userDto);
        return "redirect:/login";
    }
}
