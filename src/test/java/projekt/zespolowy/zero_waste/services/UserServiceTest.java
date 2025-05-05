package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import projekt.zespolowy.zero_waste.dto.user.UserRegistrationDto;
import projekt.zespolowy.zero_waste.dto.user.UserUpdateDto;
import projekt.zespolowy.zero_waste.entity.PrivacySettings;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;
import projekt.zespolowy.zero_waste.entity.enums.UserRole;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.repository.ChatRoomRepository;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;
import projekt.zespolowy.zero_waste.security.CustomUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private AdviceMapper adviceMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserTaskRepository userTaskRepository;


    @InjectMocks
    private UserService userService;

    // dodajemy to pole, bo w testach odwołujemy się do 'user'
    private User user;

    @BeforeEach
    void setUp() {
        // przygotowanie instancji user
        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPwd");

        // wstrzyknięcie pozostałych repozytoriów (bo UserService nie używa konstruktorowo tych dwóch)
        ReflectionTestUtils.setField(userService, "taskRepository", taskRepository);
        ReflectionTestUtils.setField(userService, "userTaskRepository", userTaskRepository);
    }

    // --- TESTY loadUserByUsername() ---

    @Test
    void testLoadUserByUsername_UserExists() {
        String username = "testUser";
        User found = new User();
        found.setUsername(username);
        found.setRole(UserRole.ROLE_USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(found));

        CustomUser customUser = (CustomUser) userService.loadUserByUsername(username);

        assertNotNull(customUser);
        assertEquals(username, customUser.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username));
    }

    // --- TESTY registerUser() ---

    @Test
    void testRegisterUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("newUser");
        dto.setEmail("new@user.com");
        dto.setPassword("plainPassword");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setBusinessAccount(false);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());
        when(taskRepository.findByTaskName("Pierwsze logowanie")).thenReturn(null);

        userService.registerUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("newUser", saved.getUsername());
        assertEquals("new@user.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());
        assertEquals(UserRole.ROLE_USER, saved.getRole());
        assertNotNull(saved.getPrivacySettings());
    }

    // --- TESTY updateUser() ---

    @Test
    void testUpdateUser_Success() {
        String currentUsername = "existingUser";
        User existingUser = new User();
        existingUser.setUsername(currentUsername);
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setPhoneNumber("111111");
        existingUser.setPassword("encodedCurrentPassword");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(currentUsername);
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");
        dto.setPhoneNumber("222222");
        dto.setCurrentPassword("currentPassword");
        dto.setNewPassword("newPassword");
        dto.setConfirmNewPassword("newPassword");

        when(userRepository.findByUsername(currentUsername))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword"))
                .thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updated = userService.updateUser(dto, currentUsername);

        assertEquals("NewFirst", updated.getFirstName());
        assertEquals("NewLast", updated.getLastName());
        assertEquals("222222", updated.getPhoneNumber());
        assertEquals("encodedNewPassword", updated.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_UsernameAlreadyExists() {
        String currentUsername = "existingUser";
        User existingUser = new User();
        existingUser.setUsername(currentUsername);
        existingUser.setPassword("encodedPass");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("newUsername");
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");
        dto.setPhoneNumber("222222");
        dto.setCurrentPassword("anyPass");

        when(userRepository.findByUsername(currentUsername))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newUsername"))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(dto, currentUsername),
                "Nazwa użytkownika już istnieje");
    }

    // --- TESTY upadateUserPhoto() ---

    @Test
    void testUpadateUserPhoto_Success() {
        String currentUsername = "userPhoto";
        User u = new User();
        u.setUsername(currentUsername);
        u.setImageUrl("oldUrl");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setImageUrl("newUrl");

        when(userRepository.findByUsername(currentUsername))
                .thenReturn(Optional.of(u));
        when(userRepository.save(u)).thenReturn(u);

        User updated = userService.upadateUserPhoto(dto, currentUsername);

        assertEquals("newUrl", updated.getImageUrl());
        verify(userRepository, times(1)).save(u);
    }

    @Test
    void testUpadateUserPhoto_UserNotFound() {
        String currentUsername = "nonexistent";
        UserUpdateDto dto = new UserUpdateDto();
        dto.setImageUrl("newUrl");

        when(userRepository.findByUsername(currentUsername))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.upadateUserPhoto(dto, currentUsername));
    }

    // --- TESTY deleteAccount() ---

    @Test
    void deleteAccount_success() {
        // Given
        when(userRepository.findByUsername("john"))
                .thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret", "encodedPwd"))
                .thenReturn(true);

        // When
        userService.deleteAccount("john", "secret");

        // Then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteAccount_userNotFound_throws() {
        // Given
        when(userRepository.findByUsername("nobody"))
                .thenReturn(java.util.Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.deleteAccount("nobody", "any"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nie znaleziono użytkownika: nobody");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteAccount_wrongPassword_throws() {
        // Given
        when(userRepository.findByUsername("john"))
                .thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPwd"))
                .thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> userService.deleteAccount("john", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nieprawidłowe hasło");

        verify(userRepository, never()).delete(any());
    }
}
