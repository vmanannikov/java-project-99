package hexlet.code.app.component;

import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setEmail("manhetan@gmail.com");
        userData.setPassword(passwordEncoder.encode("qwerty"));
        userData.setFirstName("Vadim");
        userData.setLastName("Manannikov");
        userService.createUser(userData);
    }
}
