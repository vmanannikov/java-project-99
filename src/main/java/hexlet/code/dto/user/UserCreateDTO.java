package hexlet.code.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    @NotNull
    private String email;

    private String firstName;

    private String lastName;

    @NotBlank
    @Size(min = 3)
    private String password;
}
