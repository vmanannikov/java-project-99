package hexlet.code.app.component;

import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DataInit implements ApplicationRunner {
    @Autowired
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new UserCreateDTO();
        userData.setEmail(email);
        userData.setPassword(passwordEncoder.encode("qwerty"));
        userService.createUser(userData);
    }
}
