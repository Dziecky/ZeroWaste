package projekt.zespolowy.zero_waste.services;

import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
@Service
public class RankingService {

    private final UserService userService;

    public RankingService(UserService userService) {
        this.userService = userService;
    }

    public int getUserRank(User user) {
        List<User> allUsers = userService.getAllUsers();
        allUsers.sort(Comparator.comparingInt(User::getTotalPoints).reversed());

        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(user.getId())) {
                return i + 1;
            }
        }

        return -1;
    }
}
