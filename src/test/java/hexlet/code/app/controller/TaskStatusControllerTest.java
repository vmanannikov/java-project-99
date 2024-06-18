package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.util.ModelGenerator;
import hexlet.code.app.util.UserUtils;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

class TaskStatusControllerTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private JwtRequestPostProcessor token;

    private TaskStatus taskStatus;

    private UserUtils userUtils;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject(userUtils.getAdmin().getUsername()));
        taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(taskStatus);
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {

        var result = mockMvc.perform(get("/api/task_statuses/" + taskStatus.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                a -> a.node("id").isEqualTo(taskStatus.getId()),
                a -> a.node("name").isEqualTo(taskStatus.getName()),
                a -> a.node("slug").isEqualTo(taskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatusData = Instancio.of(modelGenerator.getTaskStatusModel()).create();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusData));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findBySlug(taskStatusData.getSlug());
        assertThat(taskStatusOptional).isPresent();

        TaskStatus status = taskStatusOptional.get();

        assertThat(status.getName()).isEqualTo(taskStatusData.getName());
    }

    @Test
    public void testUpdate() throws Exception {

        TaskStatusUpdateDTO taskStatusData = new TaskStatusUpdateDTO();
        taskStatusData.setName(JsonNullable.of(FAKER.text().text(5, 10)));
        taskStatusData.setSlug(JsonNullable.of(FAKER.text().text(5, 10)));

        var request = put("/api/task_statuses/" + taskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findById(taskStatus.getId());
        assertThat(taskStatusOptional).isPresent();

        TaskStatus status = taskStatusOptional.get();

        assertThat(status.getName()).isEqualTo(taskStatusData.getName().get());
        assertThat(status.getSlug()).isEqualTo(taskStatusData.getSlug().get());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(delete("/api/task_statuses/" + taskStatus.getId()).with(token))
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.findById(taskStatus.getId())).isEmpty();
    }

    @Test
    public void testDefaultStatuses() throws Exception {
        assertThat(taskStatusRepository.findBySlug("draft")).isPresent();
        assertThat(taskStatusRepository.findBySlug("to_review")).isPresent();
        assertThat(taskStatusRepository.findBySlug("to_be_fixed")).isPresent();
        assertThat(taskStatusRepository.findBySlug("to_publish")).isPresent();
        assertThat(taskStatusRepository.findBySlug("published")).isPresent();
    }
}
