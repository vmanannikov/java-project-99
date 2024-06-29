package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("manhetan@gmail.com").isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setEmail("manhetan@gmail.com");
            userData.setPassword("qwerty");
            userData.setFirstName("Vadim");
            userData.setLastName("Manannikov");
            userService.create(userData);
        }

        var taskStatuses = new HashMap<String, String>();

        taskStatuses.put("Draft", "draft");
        taskStatuses.put("ToReview", "to_review");
        taskStatuses.put("ToBeFixed", "to_be_fixed");
        taskStatuses.put("ToPublish", "to_publish");
        taskStatuses.put("Published", "published");

        for (var status : taskStatuses.entrySet()) {
            if (taskStatusRepository.findBySlug(status.getValue()).isEmpty()) {
                var taskStatus = new TaskStatus();
                taskStatus.setName(status.getKey());
                taskStatus.setSlug(status.getValue());
                taskStatusRepository.save(taskStatus);
            }
        }

        if (labelRepository.findByName("feature").isEmpty()) {
            var l1 = new Label();
            l1.setName("feature");
            labelRepository.save(l1);
        }

        if (labelRepository.findByName("bug").isEmpty()) {
            var l2 = new Label();
            l2.setName("bug");
            labelRepository.save(l2);
        }
    }
}
