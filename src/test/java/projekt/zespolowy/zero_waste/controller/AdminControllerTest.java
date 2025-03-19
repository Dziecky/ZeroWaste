package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.UserRole;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Test
    void showUsersTest() {
        // Przygotowanie przykładowych użytkowników
        User user1 = new User();
        user1.setUsername("janek");
        user1.setFirstName("Jan");
        user1.setLastName("Kowalski");
        user1.setRole(UserRole.ROLE_USER);

        User user2 = new User();
        user2.setUsername("admin1");
        user2.setFirstName("Anna");
        user2.setLastName("Nowak");
        user2.setRole(UserRole.ROLE_ADMIN);

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Wywołanie metody
        String viewName = adminController.showUsers(model);

        // Weryfikacja
        assertEquals("User/admin/admin-users", viewName);
        verify(model).addAttribute("users", users);
    }

    @Test
    void updateUserRoleTest_UserNotFound() {
        // Przygotowanie: brak użytkownika o podanym ID
        Long userId = 1L;
        String newRole = "ROLE_ADMIN";
        when(userService.findById(userId)).thenReturn(null);

        // Wywołanie metody
        String redirect = adminController.updateUserRole(userId, newRole, redirectAttributes);

        // Weryfikacja
        assertEquals("redirect:/admin/users", redirect);
        verify(redirectAttributes).addFlashAttribute("error", "Nie znaleziono użytkownika");
    }

    @Test
    void updateUserRoleTest_Success() {
        // Przygotowanie przykładowego użytkownika z rolą ROLE_USER
        Long userId = 2L;
        String newRole = "ROLE_ADMIN";
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.ROLE_USER);

        when(userService.findById(userId)).thenReturn(user);

        // Wywołanie metody
        String redirect = adminController.updateUserRole(userId, newRole, redirectAttributes);

        // Weryfikacja: użytkownik powinien mieć zaktualizowaną rolę oraz zapisaną zmianę
        assertEquals("redirect:/admin/users", redirect);
        assertEquals(UserRole.ROLE_ADMIN, user.getRole());
        verify(userService).save(user);
        verify(redirectAttributes).addFlashAttribute("success", "Rola użytkownika została zaktualizowana");
    }
}
