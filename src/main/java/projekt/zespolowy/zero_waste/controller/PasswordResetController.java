package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.resetPassword.PasswordResetService;

@Controller
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "User/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        passwordResetService.createAndSendResetToken(email);
        model.addAttribute("message", "Jeśli adres był poprawny, wysłano link do resetu hasła.");
        return "User/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Sprawdzamy ważność tokenu
        User user = passwordResetService.validateTokenAndGetUser(token);
        if (user == null) {
            model.addAttribute("error", "Token nieprawidłowy lub wygasł.");
            return "User/reset-password";
        }

        model.addAttribute("token", token);
        return "User/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Hasła nie są identyczne");
            model.addAttribute("token", token);
            return "User/reset-password";
        }

        User user = passwordResetService.validateTokenAndGetUser(token);
        if (user == null) {
            model.addAttribute("error", "Token nieprawidłowy lub wygasł.");
            return "User/reset-password";
        }

        userService.updatePassword(user, password);
        passwordResetService.markTokenAsUsed(token);

        model.addAttribute("message", "Hasło zostało zresetowane, możesz się zalogować.");
        return "redirect:/login"; // lub przekierowanie na stronę logowania
    }
}
