package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelsGenerator;

    private User testUser;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUser() {
        testUser = Instancio.of(modelsGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Order(1)
    @Test
    public void testIndex() throws Exception {
        userRepository.save(testUser);
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Order(2)
    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        var request = get("/api/users/" + testUser.getId()).with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()));
    }

    @Order(3)
    @Test
    public void testCreate() throws Exception {
        var createDTO = userMapper.map(testUser);

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        mockMvc.perform(request)
             .andExpect(status().isCreated());
        var user = userRepository.findByEmail(testUser.getEmail()).orElse(null);
        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Order(4)
    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);

        var updateDto = new UserUpdateDTO();
        updateDto.setFirstName(JsonNullable.of("Fake name"));
        updateDto.setLastName(JsonNullable.of("Fake lastName"));

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).get();

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(updateDto.getFirstName().get());
        assertThat(user.getLastName()).isEqualTo(updateDto.getLastName().get());
    }

    @Order(5)
    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);

        var request = delete("/api/users/" + testUser.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }

    @Order(6)
    @Test
    public void testIndexWithoutAuth() throws Exception {
        userRepository.save(testUser);
        mockMvc.perform(get("/users"))
               .andExpect(status().isUnauthorized());

    }

    @Order(7)
    @Test
    public void testShowWithoutAuth() throws Exception {
        userRepository.save(testUser);

        var request = get("/users/{id}", testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
