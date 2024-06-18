package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
import net.datafaker.Faker;
import net.javacrumbs.jsonunit.core.Option;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.util.HashMap;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper om;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private User testUser;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void setUp() {
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var tasks = om.readValue(body, new TypeReference<List<Task>>() { });
        var expected = taskRepository.findAll();

        assertThat(tasks).containsAll(expected);
    }

    @Test
    public void testIndexWithFilter() throws Exception {
        var titleCont = testTask.getName().substring(1).toLowerCase();
        var assigneeId = testTask.getAssignee().getId();
        var status = testTask.getTaskStatus().getSlug();
        var labelId = testTask.getLabels().iterator().next().getId();

        var wrongTask = Instancio.of(modelGenerator.getTaskModel()).create();
        taskRepository.save(wrongTask);

        var request = get("/api/tasks"
                + "?"
                + "name=" + titleCont
                + "&assigneeId=" + assigneeId
                + "&status=" + status
                + "&labelId=" + labelId)
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var data = new HashMap<>();
        data.put("assignee_id", testTask.getAssignee().getId());
        data.put("content", testTask.getDescription());
        data.put("createdAt", testTask.getCreatedAt());
        data.put("id", testTask.getId());
        data.put("index", testTask.getIndex());
        data.put("status", testTask.getTaskStatus().getSlug());
        data.put("title", testTask.getName());
        data.put("taskLabelIds", List.of(labelId));

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).when(Option.IGNORING_ARRAY_ORDER)
                .isArray()
                .contains(om.writeValueAsString(data));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                jsonAssert -> jsonAssert.node("id").isEqualTo(testTask.getId()),
                jsonAssert -> jsonAssert.node("title").isEqualTo(testTask.getName()),
                jsonAssert -> jsonAssert.node("content").isEqualTo(testTask.getDescription()),
                jsonAssert -> jsonAssert.node("index").isEqualTo(testTask.getIndex()),
                jsonAssert -> jsonAssert.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                jsonAssert -> jsonAssert.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                jsonAssert -> jsonAssert.node("createdAt")
                        .isEqualTo(testTask.getCreatedAt())
        );

        var receivedTask = om.readValue(body, Task.class);
        assertThat(receivedTask).isEqualTo(testTask);
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("title", testTask.getName());
        data.put("index", testTask.getIndex());
        data.put("content", testTask.getDescription());
        data.put("status", testTask.getTaskStatus());
        data.put("assignee_id", testTask.getAssignee());

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName(testTask.getName()).get();

        assertNotNull(task);
        assertThat(taskRepository.findByName(testTask.getName())).isPresent();

        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getAssignee()).isEqualTo(testTask.getAssignee());
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
    }

    @Test
    public void testUpdate() throws Exception {
        var oldDescription = testTask.getDescription();
        var newDescription = faker.lorem().sentence();

        var data = new HashMap<>();
        data.put("content", newDescription);

        token = jwt().jwt(builder -> builder.subject(oldDescription));

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findByName(testTask.getName()).get();

        assertThat(task.getDescription()).isEqualTo(newDescription);
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getAssignee()).isEqualTo(testTask.getAssignee());
    }

    @Test
    public void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }
}