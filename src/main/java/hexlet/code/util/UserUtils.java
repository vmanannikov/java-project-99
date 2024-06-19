package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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
        admin.setPasswordDigest("qwerty");
        return admin;
    }
}
