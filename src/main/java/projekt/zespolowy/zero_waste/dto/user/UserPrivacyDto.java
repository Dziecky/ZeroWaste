package projekt.zespolowy.zero_waste.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;

@Data
@NotNull
public class UserPrivacyDto {

    private PrivacyOptions phoneVisible;
    private PrivacyOptions emailVisible;
    private PrivacyOptions surnameVisible;

}
