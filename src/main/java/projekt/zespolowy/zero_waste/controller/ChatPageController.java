package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    @GetMapping("/chat")
    public String chatPage(Model model) {
        String username = "User1";
        model.addAttribute("username", username);
        return "chat";
    }
}
