package hexlet.code.dto.auth;

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
