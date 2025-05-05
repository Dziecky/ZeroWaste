package projekt.zespolowy.zero_waste.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;
import projekt.zespolowy.zero_waste.entity.enums.UserRole;
import projekt.zespolowy.zero_waste.services.AdminService;
import projekt.zespolowy.zero_waste.services.RefundService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RefundService refundService;
    private final AdminService adminService;

    public AdminController(UserService userService, RefundService refundService, AdminService adminService) {
        this.userService = userService;
        this.refundService = refundService;
        this.adminService = adminService;
    }

    // Wyświetlenie listy użytkowników
    @GetMapping("/users")
    public String showUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Page<User> userPage = userService.getUsersPaginated(page - 1, size, search);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("baseUrl", "/admin/users");
        return "User/admin/admin-users";
    }


    // Endpoint do aktualizacji roli użytkownika
    @PostMapping("/updateUserRole")
    public String updateUserRole(@RequestParam Long userId, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono użytkownika");
            return "redirect:/admin/users";
        }
        // Aktualizacja roli – przyjmujemy, że użytkownik może mieć tylko jedną rolę
        user.setRole(UserRole.valueOf(newRole));
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Rola użytkownika została zaktualizowana");
        return "redirect:/admin/users";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long userId,
                             @RequestParam String adminPassword,
                             RedirectAttributes ra) {
        try {
            adminService.deleteUserByAdmin(userId, adminPassword);
            ra.addFlashAttribute("success", "Użytkownik został usunięty");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/refunds")
    public String showRefunds(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<Refund> refundPage = refundService.getAllRefunds(page - 1, size);
        model.addAttribute("refunds", refundPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", refundPage.getTotalPages());
        model.addAttribute("baseUrl", "/admin/refunds");
        return "User/admin/admin-refunds";
    }

    @GetMapping("/refundDetails/{id}")
    public String getRefundDetails(@PathVariable Long id, Model model) {
        Refund refund = refundService.findRefundById(id);
        model.addAttribute("refund", refund);
        return "User/admin/admin-refund-details";
    }

    @PostMapping("/updateRefundStatus")
    public String updateRefundStatus(@RequestParam Long refundId, @RequestParam RefundStatus newStatus) {
        refundService.updateRefundStatus(refundId, newStatus);
        return "redirect:/admin/refundDetails/" + refundId;
    }
}

