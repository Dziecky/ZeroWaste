package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
public class RankingController {
    private final UserService userService;

    public RankingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/ranking")
    public String showGlobalRanking(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "sortBy", required = false, defaultValue = "totalPoints") String sortBy,
            Model model) {

        // Pobierz użytkowników z miejscem w rankingu, filtrowaniem i sortowaniem
        List<User> rankedUsers = userService.getRankedAndFilteredUsers(search, sortBy);

        // Przekaż dane do modelu
        model.addAttribute("ranking", rankedUsers);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);

        return "ranking";
    }
}
