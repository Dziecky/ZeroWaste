package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import projekt.zespolowy.zero_waste.dto.user.UserRegistrationDto;
import projekt.zespolowy.zero_waste.dto.user.UserUpdateDto;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.entity.enums.AuthProvider;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;

import java.util.List;
import projekt.zespolowy.zero_waste.security.CustomUser;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    // Konstruktorowe wstrzykiwanie zależności
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Implementacja metody z UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika: " + username);
        }
        return new CustomUser(user);
    }

    // Zapisywanie nowego użytkownika po zakodowaniu hasła
    public void registerUser(UserRegistrationDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setProvider(AuthProvider.LOCAL);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAccountType(userDto.isBusinessAccount() ? AccountType.BUSINESS : AccountType.NORMAL);
        userRepository.save(user);

        // Pobierz wszystkie istniejące zadania
        List<Task> allTasks = taskRepository.findAll();

        // Dla każdego zadania przypisz je do nowego użytkownika
        for (Task task : allTasks) {
            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTask(task);
            userTask.setProgress(0);
            userTask.setCompleted(false);
            userTask.setCompletionDate(null);

            // Zapisz obiekt UserTask w bazie
            userTaskRepository.save(userTask);
        }
    }

    public List<UserTask> getUserTasksForUser(User user) {
        return userTaskRepository.findByUser(user);
    }

    // Znajdź użytkownika po nazwie użytkownika
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Znajdź użytkownika po emailu
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User updateUser(UserUpdateDto userUpdateDto, String currentUsername) {
        User user = findByUsername(currentUsername);
        if (user == null) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika: " + currentUsername);
        }
        if (!user.getUsername().equals(userUpdateDto.getUsername())) {
            if (findByUsername(userUpdateDto.getUsername()) != null) {
                throw new IllegalArgumentException("Nazwa użytkownika już istnieje");
            }
            user.setUsername(userUpdateDto.getUsername());
        }

        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setPhoneNumber(userUpdateDto.getPhoneNumber());

        if (userUpdateDto.getNewPassword() != null && !userUpdateDto.getNewPassword().isEmpty()) {
            if (userUpdateDto.getCurrentPassword() == null || userUpdateDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("Obecne hasło jest wymagane do zmiany hasła");
            }
            if (!passwordEncoder.matches(userUpdateDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Obecne hasło jest nieprawidłowe");
            }
            if (!userUpdateDto.getNewPassword().equals(userUpdateDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Nowe hasła nie są zgodne");
            }
            user.setPassword(passwordEncoder.encode(userUpdateDto.getNewPassword()));
        }
        return userRepository.save(user);
    }
}
