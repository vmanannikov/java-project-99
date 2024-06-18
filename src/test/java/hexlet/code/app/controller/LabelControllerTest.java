package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    ModelGenerator modelGenerator;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Label testLabel;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var labels = om.readValue(body, new TypeReference<List<Label>>() { });
        var expected = labelRepository.findAll();

        assertThat(labels.containsAll(expected));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/" + testLabel.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testLabel.getId()),
                json -> json.node("name").isEqualTo(testLabel.getName()),
                json -> json.node("createdAt").isEqualTo(testLabel.getCreatedAt().format(modelGenerator.FORMATTER))
        );

        var receivedLabel = om.readValue(body, Label.class);
        assertThat(receivedLabel).isEqualTo(testLabel);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel()).create();

        var labelsCount = labelRepository.count();

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(labelRepository.count()).isEqualTo(labelsCount + 1);

        var addedLabel = labelRepository.findByName(data.getName()).get();

        assertNotNull(addedLabel);
        assertThat(labelRepository.findByName(testLabel.getName())).isPresent();

        assertThat(addedLabel.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var oldName = testLabel.getName();
        var newName = faker.lorem().word();

        var data = new HashMap<>();
        data.put("name", newName);

        var labelsCount = labelRepository.count();

        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(labelRepository.count()).isEqualTo(labelsCount);

        var label = labelRepository.findById(testLabel.getId()).get();

        assertThat(label.getName()).isEqualTo(newName);
        assertThat(labelRepository.findByName(oldName)).isEmpty();
    }

    @Test
    public void testDestroy() throws Exception {
        var labelsCount = labelRepository.count();

        token = jwt().jwt(builder -> builder.subject(testLabel.getName()));

        mockMvc.perform(delete("/api/labels/" + testLabel.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.count()).isEqualTo(labelsCount - 1);
        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }

    @Test
    public void testDestroyLabelWhichIsUsing() throws Exception {
        var testTask = modelGenerator.getTestTask();
        taskRepository.save(testTask);

        var taskLabel = testTask.getLabels().iterator().next();

        mockMvc.perform(delete("/api/labels/" + taskLabel.getId()).with(token))
                .andExpect(status().isInternalServerError());

        assertThat(labelRepository.findById(taskLabel.getId())).isPresent();
    }

    @AfterEach
    public void clean() {
        labelRepository.deleteAll();
    }
}
