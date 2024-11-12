package projekt.zespolowy.zero_waste.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.Announcement;
import projekt.zespolowy.zero_waste.repository.AnnouncementRepository;

@Controller
@AllArgsConstructor
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @GetMapping("/announcements")
    public String showAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        return "announcements";
    }

    @PostMapping("/announcements")
    public String submitAnnouncement(@ModelAttribute Announcement announcement) {
        announcementRepository.save(announcement);
        return "announcements";
    }
}
