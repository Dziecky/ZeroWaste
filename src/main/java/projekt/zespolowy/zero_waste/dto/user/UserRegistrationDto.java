package projekt.zespolowy.zero_waste.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotEmpty(message = "Nazwa użytkownika jest wymagana")
    private String username;

    @NotEmpty(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    private String email;

    @NotEmpty(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    private String password;

    @NotEmpty(message = "Imię jest wymagane")
    private String firstName;

    @NotEmpty(message = "Nazwisko jest wymagane")
    private String lastName;

    private boolean businessAccount;
}
