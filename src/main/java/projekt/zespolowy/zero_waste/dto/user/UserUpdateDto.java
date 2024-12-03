package projekt.zespolowy.zero_waste.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @NotEmpty(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotEmpty(message = "Imię jest wymagane")
    private String firstName;

    @NotEmpty(message = "Nazwisko jest wymagane")
    private String lastName;

    @NotEmpty(message = "Numer telefonu jest wymagany")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Nieprawidłowy format numeru telefonu")
    private String phoneNumber;

    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;
}
