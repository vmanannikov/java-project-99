package hexlet.code.app.component;

import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setEmail("manhetan@gmail.com");
        userData.setPassword("qwerty");
        userData.setFirstName("Vadim");
        userData.setLastName("Manannikov");
        userService.createUser(userData);
    }
}
