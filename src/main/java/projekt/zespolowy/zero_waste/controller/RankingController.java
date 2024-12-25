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
import projekt.zespolowy.zero_waste.services.chat.ChatRoomService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RankingController {
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public RankingController(UserService userService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
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

        return "Ranking/ranking";
    }

    @GetMapping("/friends-ranking")
    public String showFriendsRanking(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "sortBy", required = false, defaultValue = "totalPoints") String sortBy,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User loggedUser = customUser.getUser();

        // Pobierz listę znajomych
        List<User> friends = chatRoomService.getFriends(loggedUser);

        // Obliczanie rankingu użytkownika przed filtrami
        int userRank = 0;
        int rankSize = friends.size();

        // Sprawdzanie, w jakiej pozycji w rankingu jest aktualnie zalogowany użytkownik
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getId().equals(loggedUser.getId())) {
                userRank = i + 1; // Ranking zaczyna się od 1
                break;
            }
        }

        // Zastosowanie filtrów wyszukiwania
        if (!search.isEmpty()) {
            friends = friends.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                            user.getLastName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Sortowanie listy użytkowników
        if (sortBy.equals("totalPoints")) {
            friends.sort(Comparator.comparingInt(User::getTotalPoints).reversed());
        } else if (sortBy.equals("username")) {
            friends.sort(Comparator.comparing(User::getUsername));
        } else if (sortBy.equals("name")) {
            friends.sort(Comparator.comparing(user -> user.getFirstName() + " " + user.getLastName()));
        }

        // Przekazanie danych do widoku
        model.addAttribute("rankSize", rankSize);
        model.addAttribute("friendsRanking", friends);
        model.addAttribute("userRank", userRank);
        model.addAttribute("loggedUserId", loggedUser.getId());
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);

        return "Ranking/friends-ranking";
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
