package projekt.zespolowy.zero_waste.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;
import projekt.zespolowy.zero_waste.security.CustomUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private static UserRepository userRepository; // Zwróć uwagę na statyczność – w testach można to obsłużyć przez Mockito

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

    @BeforeEach
    void setUp() {
        // Resetujemy statyczny mock (jeśli potrzeba)
        reset(userRepository);
        ReflectionTestUtils.setField(userService, "taskRepository", taskRepository);
        ReflectionTestUtils.setField(userService, "userTaskRepository", userTaskRepository);
    }

    // Test metody loadUserByUsername, gdy użytkownik istnieje
    @Test
    void testLoadUserByUsername_UserExists() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setRole(UserRole.ROLE_USER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        CustomUser customUser = (CustomUser) userService.loadUserByUsername(username);
        assertNotNull(customUser);
        assertEquals(username, customUser.getUsername());
    }

    // Test metody loadUserByUsername, gdy użytkownik nie istnieje
    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    // Test metody registerUser
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
//        when(taskRepository.findAll()).thenReturn(Collections.emptyList());
//        when(taskRepository.findByTaskName("Pierwsze logowanie")).thenReturn(null);
        // Metoda save ma zwracać przekazanego użytkownika (dla uproszczenia)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(dto);

        // Używamy ArgumentCaptor do przechwycenia argumentu przekazanego do userRepository.save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newUser", savedUser.getUsername());
        assertEquals("new@user.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(UserRole.ROLE_USER, savedUser.getRole());
        assertNotNull(savedUser.getPrivacySettings());
    }

    // Test metody updateUser - poprawna aktualizacja, w tym zmiana hasła
    @Test
    void testUpdateUser_Success() {
        String currentUsername = "existingUser";
        User existingUser = new User();
        existingUser.setUsername(currentUsername);
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setPhoneNumber("111111");
        existingUser.setPassword("encodedCurrentPassword");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername(currentUsername);
        updateDto.setFirstName("NewFirst");
        updateDto.setLastName("NewLast");
        updateDto.setPhoneNumber("222222");
        updateDto.setCurrentPassword("currentPassword");
        updateDto.setNewPassword("newPassword");
        updateDto.setConfirmNewPassword("newPassword");

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(updateDto, currentUsername);

        assertEquals("NewFirst", updatedUser.getFirstName());
        assertEquals("NewLast", updatedUser.getLastName());
        assertEquals("222222", updatedUser.getPhoneNumber());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    // Test metody updateUser - zmiana nazwy użytkownika na istniejącą (błąd)
    @Test
    void testUpdateUser_UsernameAlreadyExists() {
        String currentUsername = "existingUser";
        User existingUser = new User();
        existingUser.setUsername(currentUsername);
        existingUser.setPassword("encodedPass");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("newUsername"); // chcemy zmienić nazwę
        updateDto.setFirstName("NewFirst");
        updateDto.setLastName("NewLast");
        updateDto.setPhoneNumber("222222");
        updateDto.setCurrentPassword("anyPass");

        // Symulujemy, że inny użytkownik już posiada nazwę "newUsername"
        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(updateDto, currentUsername));
        assertTrue(exception.getMessage().contains("Nazwa użytkownika już istnieje"));
    }

    // Test metody upadateUserPhoto - poprawna aktualizacja zdjęcia
    @Test
    void testUpadateUserPhoto_Success() {
        String currentUsername = "userPhoto";
        User user = new User();
        user.setUsername(currentUsername);
        user.setImageUrl("oldUrl");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setImageUrl("newUrl");

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.upadateUserPhoto(updateDto, currentUsername);

        assertEquals("newUrl", updatedUser.getImageUrl());
        verify(userRepository, times(1)).save(user);
    }

    // Test metody upadateUserPhoto - użytkownik nie znaleziony
    @Test
    void testUpadateUserPhoto_UserNotFound() {
        String currentUsername = "nonexistent";
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setImageUrl("newUrl");

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.upadateUserPhoto(updateDto, currentUsername));
    }
}
