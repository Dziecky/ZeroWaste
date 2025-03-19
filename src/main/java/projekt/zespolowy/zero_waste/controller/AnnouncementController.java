package projekt.zespolowy.zero_waste.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.Announcement;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.AnnouncementRepository;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/announcements") // Base path for announcements
public class AnnouncementController {

    @Autowired
    private final AnnouncementRepository announcementRepository;
    @Autowired
    private final ProductService productService; // Inject ProductService

    @GetMapping
    public String showAnnouncements(
            @RequestParam(name = "productSearch", required = false) String productSearch,
            @RequestParam(name = "myAnnouncementsOnly", required = false, defaultValue = "false") boolean myAnnouncementsOnly,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Model model) {
        User user = UserService.getUser();

        // Fetch products for dropdown
        List<Product> products = productService.getAllProducts();

        List<Announcement> announcements = myAnnouncementsOnly
                ? announcementRepository.findByOwner(user)
                : announcementRepository.findAll();

        // Filter announcements by product name if provided
        if (productSearch != null && !productSearch.isEmpty()) {
            announcements = announcements.stream()
                    .filter(a -> a.getProducts().stream()
                            .anyMatch(p -> p.getName().toLowerCase().contains(productSearch.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Sort announcements based on the sort parameter
        switch (sort) {
            case "mostViewed":
                announcements.sort((a1, a2) -> Integer.compare(
                        a2.getViewedByUsers().size(),
                        a1.getViewedByUsers().size()));
                break;
            case "leastViewed":
                announcements.sort((a1, a2) -> Integer.compare(
                        a1.getViewedByUsers().size(),
                        a2.getViewedByUsers().size()));
                break;
            case "oldest":
                announcements.sort((a1, a2) -> a1.getCreatedAt().compareTo(a2.getCreatedAt()));
                break;
            case "newest":
            default:
                announcements.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
                break;
        }

        // Pagination logic
        int totalAnnouncements = announcements.size();
        int totalPages = (int) Math.ceil((double) totalAnnouncements / size);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalAnnouncements);

        List<Announcement> paginatedAnnouncements = fromIndex < totalAnnouncements
                ? announcements.subList(fromIndex, toIndex)
                : List.of();

        model.addAttribute("products", products);
        model.addAttribute("announcements", paginatedAnnouncements);
        model.addAttribute("myAnnouncementsOnly", myAnnouncementsOnly);
        model.addAttribute("productSearch", productSearch);
        model.addAttribute("accountType", user.getAccountType().toString());
        model.addAttribute("currentUser", user);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("selectedSort", sort);

        return "/Announcement/announcements";
    }

    // Render the form for creating a new announcement
    @GetMapping("/create")
    public String createAnnouncementView(Model model) {
        model.addAttribute("announcement", new Announcement());
        model.addAttribute("products", productService.getAllProducts()); // Assuming ProductService exists
        return "/Announcement/createAnnouncement";
    }

    // Handle form submission for new announcements
    @PostMapping
    public String submitAnnouncement(@ModelAttribute Announcement announcement,
                                     @RequestParam("productIds") List<Long> productIds) {
        User currentUser = UserService.getUser(); // Retrieve the current logged-in user

        // Set the owner of the announcement
        announcement.setOwner(currentUser);

        // Set selected products
        List<Product> selectedProducts = productService.getProductsByIds(productIds);
        announcement.setProducts(selectedProducts);

        // Save announcement
        announcementRepository.save(announcement);

        return "redirect:/announcements";
    }

    // Display details for a single announcement
    @GetMapping("/{id}")
    public String showAnnouncementDetails(@PathVariable Long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement ID: " + id));

        User currentUser = UserService.getUser();
        
        // Add view if user hasn't viewed before
        if (!announcement.getViewedByUsers().contains(currentUser)) {
            announcement.getViewedByUsers().add(currentUser);
            currentUser.getViewedAnnouncements().add(announcement);
            announcementRepository.save(announcement);
        }

        // Format dates as strings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtFormatted = announcement.getCreatedAt().format(formatter);
        String updatedAtFormatted = announcement.getUpdatedAt().format(formatter);

        model.addAttribute("announcement", announcement);
        model.addAttribute("createdAtFormatted", createdAtFormatted);
        model.addAttribute("updatedAtFormatted", updatedAtFormatted);
        model.addAttribute("viewCount", announcement.getViewedByUsers().size());

        return "/Announcement/details";
    }
    @GetMapping("/my-announcements")
    public String viewMyAnnouncements(Model model) {
        // Fetch announcements for the logged-in user
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) auth.getPrincipal();
//
//        List<Announcement> myAnnouncements = announcementService.findByOwner(currentUser);
//        model.addAttribute("announcements", myAnnouncements);

        return "my-announcements"; // Thymeleaf view for displaying user's announcements
    }

    @DeleteMapping("/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        User currentUser = UserService.getUser();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement ID: " + id));

        // Ensure that only the owner can delete the announcement
        if (!announcement.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not allowed to delete this announcement.");
        }

        announcementRepository.delete(announcement);
        return "redirect:/announcements";
    }

    @PostMapping("/{id}/upvote")
    @ResponseBody
    public ResponseEntity<?> upvoteAnnouncement(@PathVariable Long id) {
        User currentUser = UserService.getUser();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement ID: " + id));

        // Check if already upvoted
        if (announcement.getUpvotedByUsers().contains(currentUser)) {
            // Remove upvote
            announcement.getUpvotedByUsers().remove(currentUser);
            currentUser.getUpvotedAnnouncements().remove(announcement);
            announcementRepository.save(announcement);
            return ResponseEntity.ok(Map.of(
                "upvotes", announcement.getUpvotedByUsers().size(),
                "downvotes", announcement.getDownvotedByUsers().size()
            ));
        }

        // Remove downvote if exists
        if (announcement.getDownvotedByUsers().contains(currentUser)) {
            announcement.getDownvotedByUsers().remove(currentUser);
            currentUser.getDownvotedAnnouncements().remove(announcement);
        }

        // Add upvote
        announcement.getUpvotedByUsers().add(currentUser);
        currentUser.getUpvotedAnnouncements().add(announcement);

        announcementRepository.save(announcement);
        return ResponseEntity.ok(Map.of(
            "upvotes", announcement.getUpvotedByUsers().size(),
            "downvotes", announcement.getDownvotedByUsers().size()
        ));
    }

    @PostMapping("/{id}/downvote")
    @ResponseBody
    public ResponseEntity<?> downvoteAnnouncement(@PathVariable Long id) {
        User currentUser = UserService.getUser();
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement ID: " + id));

        // Check if already downvoted
        if (announcement.getDownvotedByUsers().contains(currentUser)) {
            // Remove downvote
            announcement.getDownvotedByUsers().remove(currentUser);
            currentUser.getDownvotedAnnouncements().remove(announcement);
            announcementRepository.save(announcement);
            return ResponseEntity.ok(Map.of(
                "upvotes", announcement.getUpvotedByUsers().size(),
                "downvotes", announcement.getDownvotedByUsers().size()
            ));
        }

        // Remove upvote if exists
        if (announcement.getUpvotedByUsers().contains(currentUser)) {
            announcement.getUpvotedByUsers().remove(currentUser);
            currentUser.getUpvotedAnnouncements().remove(announcement);
        }

        // Add downvote
        announcement.getDownvotedByUsers().add(currentUser);
        currentUser.getDownvotedAnnouncements().add(announcement);

        announcementRepository.save(announcement);
        return ResponseEntity.ok(Map.of(
            "upvotes", announcement.getUpvotedByUsers().size(),
            "downvotes", announcement.getDownvotedByUsers().size()
        ));
    }
}

