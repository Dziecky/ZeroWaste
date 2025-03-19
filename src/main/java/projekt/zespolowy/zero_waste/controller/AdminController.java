package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.UserRole;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Wyświetlenie listy użytkowników
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers(); // Metoda, która pobiera wszystkich użytkowników
        model.addAttribute("users", users);
        return "User/admin/admin-users"; // Nazwa szablonu Thymeleaf
    }

    // Endpoint do aktualizacji roli użytkownika
    @PostMapping("/updateUserRole")
    public String updateUserRole(@RequestParam Long userId, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono użytkownika");
            return "redirect:/admin/users";
        }
        // Aktualizacja roli – przyjmujemy, że użytkownik może mieć tylko jedną rolę
        user.setRole(UserRole.valueOf(newRole));
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Rola użytkownika została zaktualizowana");
        return "redirect:/admin/users";
    }
}

