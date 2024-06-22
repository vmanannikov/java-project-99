package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.UserUtils;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserUtils userUtils;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(userUtils.getAdmin().getPassword()));
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
    }

    @AfterEach
    public void cleanUp() {
        modelGenerator.clean();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var users = om.readValue(body, new TypeReference<List<User>>() { });
        var expected = userRepository.findAll();

        assertThatJson(body).isArray();
        assertThat(users).containsAll(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testUser.getId()),
                json -> json.node("firstName").isEqualTo(testUser.getFirstName()),
                json -> json.node("lastName").isEqualTo(testUser.getLastName()),
                json -> json.node("email").isEqualTo(testUser.getEmail()),
                json -> json.node("createdAt").isEqualTo(testUser.getCreatedAt().format(ModelGenerator.FORMATTER))
        );

        var receivedUser = om.readValue(body, User.class);
        assertThat(receivedUser).isEqualTo(testUser);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var usersCount = userRepository.count();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(userRepository.count()).isEqualTo(usersCount + 1);

        var user = userRepository.findByEmail(data.getEmail()).get();

        assertNotNull(user);
        assertThat(userRepository.findByEmail(testUser.getEmail())).isPresent();

        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
        assertThat(user.getEmail()).isEqualTo(data.getEmail());
    }

    @Test
    public void testUpdate() throws Exception {
        var userPassword = testUser.getPassword();

        var oldEmail = testUser.getEmail();
        var newEmail = faker.internet().emailAddress();
        var data = new HashMap<>();
        data.put("firstName", "Hope");
        data.put("email", newEmail);

        var usersCount = userRepository.count();

        token = jwt().jwt(builder -> builder.subject(oldEmail));

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(userRepository.count()).isEqualTo(usersCount);

        var user = userRepository.findById(testUser.getId()).get();

        assertThat(user.getFirstName()).isEqualTo(("Hope"));
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(userRepository.findByEmail(oldEmail)).isEmpty();
        assertThat(userRepository.findByEmail(newEmail)).get().isEqualTo(user);

        var userHashedPassword = user.getPassword();
        assertThat(userPassword).isNotEqualTo(userHashedPassword);
    }

    @Test
    public void testDestroy() throws Exception {
        var usersCount = userRepository.count();

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.count()).isEqualTo(usersCount - 1);
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
