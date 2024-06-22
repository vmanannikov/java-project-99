package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setEmail("manhetan@gmail.com");
        userData.setPassword(passwordEncoder.encode("qwerty"));
        userData.setFirstName("Vadim");
        userData.setLastName("Manannikov");
        userService.create(userData);

        var taskStatuses = new HashMap<String, String>();

        taskStatuses.put("Draft", "draft");
        taskStatuses.put("ToReview", "to_review");
        taskStatuses.put("ToBeFixed", "to_be_fixed");
        taskStatuses.put("ToPublish", "to_publish");
        taskStatuses.put("Published", "published");

        taskStatuses.entrySet().stream()
                .map(entry -> {
                    var taskStatus = new TaskStatus();
                    taskStatus.setName(entry.getKey());
                    taskStatus.setSlug(entry.getValue());
                    return taskStatus;
                })
                .forEach(taskStatusRepository::save);

        var l1 = new Label();
        var l2 = new Label();

        l1.setName("feature");
        l2.setName("bug");

        labelRepository.save(l1);
        labelRepository.save(l2);
    }
}
