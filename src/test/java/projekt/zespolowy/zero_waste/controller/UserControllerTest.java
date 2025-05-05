package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;
import projekt.zespolowy.zero_waste.services.ReviewService;
import projekt.zespolowy.zero_waste.services.UserService;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ReviewService reviewService;              // ← dorzuć

    @MockBean
    private UserPreferenceService userPreferenceService; // ← i to

    @MockBean
    private UserRepository userRepository;            // ← oraz to

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "testuser")
    void handleDeleteAccount_success_redirectsHomeWithInfoFlash() throws Exception {
        mockMvc.perform(post("/deleteAccount")
                        .param("password", "rawPwd")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("info", "Twoje konto zostało usunięte."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void handleDeleteAccount_invalidPassword_redirectsBackWithErrorFlash() throws Exception {
        doThrow(new IllegalArgumentException("Nieprawidłowe hasło"))
                .when(userService).deleteAccount("testuser", "wrong");

        mockMvc.perform(post("/deleteAccount")
                        .param("password", "wrong")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/deleteAccount"))
                .andExpect(flash().attribute("error", "Nieprawidłowe hasło"));
    }
}
