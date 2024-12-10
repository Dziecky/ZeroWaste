package projekt.zespolowy.zero_waste.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean businessAccount;
}
