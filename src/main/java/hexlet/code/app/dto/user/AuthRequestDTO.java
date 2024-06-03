package hexlet.code.app.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
