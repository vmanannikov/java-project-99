package hexlet.code.app.component;

import hexlet.code.app.model.User;
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
        var userData = new User();
        userData.setEmail(email);
        userData.setPasswordDigest(passwordEncoder.encode("qwerty"));
        userService.create(userData);

/*        var user = userService.findByEmail(email).get();

        var faker = new Faker();
        IntStream.range(1, 10).forEach(i -> {
            var user = new User();
            post.setName(faker.book().title());
            var paragraphs = faker.lorem().paragraphs(5);
            post.setBody(String.join("\n", paragraphs));
            post.setSlug(faker.internet().slug());
            post.setAuthor(user);
            userService.create(user);
        });*/
    }
}
