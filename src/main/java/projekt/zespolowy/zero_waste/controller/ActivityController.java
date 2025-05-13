package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.dto.user.ActivityLogDto;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.repository.ActivityLogRepository;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/me/activity")
public class ActivityController {

    @Autowired
    private ActivityLogRepository logRepository;

    @GetMapping
    public String myActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ActivityType filter,
            Model model
    ) {
        User user = UserService.getUser();
        Long userId = user.getId();

        Pageable pg = PageRequest.of(page, size, Sort.by("occurredAt").descending());

        Page<ActivityLogDto> logsPage = (filter == null)
                ? logRepository.findByUserId(userId, pg).map(ActivityLogDto::fromEntity)
                : logRepository.findByUserIdAndActivityType(userId, filter, pg).map(ActivityLogDto::fromEntity);

        model.addAttribute("logsPage", logsPage);
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", logsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("filter", filter);
        model.addAttribute("activityTypes", ActivityType.values());
        model.addAttribute("baseUrl", "/me/activity");
        return "activity/history";
    }
}
