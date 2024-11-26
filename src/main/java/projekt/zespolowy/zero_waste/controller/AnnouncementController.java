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
@RequestMapping("/announcements") // Base path for announcements
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    // Display all announcements
    @GetMapping
    public String showAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        return "/Announcement/announcements";
    }

    // Render the form for creating a new announcement
    @GetMapping("/create")
    public String createAnnouncementView() {
        return "/Announcement/createAnnouncement";
    }

    // Handle form submission for new announcements
    @PostMapping
    public String submitAnnouncement(@ModelAttribute Announcement announcement) {
        announcementRepository.save(announcement);
        return "redirect:/announcements";
    }

    // Display details for a single announcement
    @GetMapping("/{id}")
    public String showAnnouncementDetails(@PathVariable Long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement ID: " + id));
        model.addAttribute("announcement", announcement);
        return "/Announcement/details"; // Path to the details template
    }



}

