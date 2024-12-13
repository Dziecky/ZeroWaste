package projekt.zespolowy.zero_waste.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.security.CustomUser;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Comparator;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User loggedUser = customUser.getUser();

        List<User> rankedUsers = userService.getRankedAndFilteredUsers(search, sortBy);

        List<User> allUsers = userService.getAllUsers();

        int userRank = 0;
        int rankSize = allUsers.size();
        allUsers.sort(Comparator.comparingInt(User::getTotalPoints).reversed());
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(loggedUser.getId())) {
                userRank = i + 1;
                break;
            }
        }

        model.addAttribute("rankSize", rankSize);
        model.addAttribute("ranking", rankedUsers);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("userRank", userRank);
        model.addAttribute("loggedUserId", loggedUser.getId());
        System.out.println(loggedUser.getId());

        return "ranking";
    }

    @GetMapping("/user/profile/{userId}")
    public String showUserProfile(@PathVariable Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User loggedUser = customUser.getUser();

        User user = userService.findById(userId);

        if (user == null) {
            return "redirect:/error";
        }

        if (loggedUser.getId().equals(userId)) {
            return "redirect:/account";
        }

        model.addAttribute("user", user);

        return "user-profile";
    }

    @GetMapping("/account")
    public String accountDetails(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();

        model.addAttribute("user", user);

        return "User/accountDetails";
    }
}
