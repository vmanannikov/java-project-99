package hexlet.code.app.util;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public User getAdmin() {
        var admin = new User();
        admin.setEmail("manhetan@gmail.com");
        admin.setPassword("qwerty");
        return admin;
    }
}
