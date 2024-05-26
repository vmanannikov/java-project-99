package hexlet.code.app;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	void testCreateUser() throws Exception {
		String userJson = "{\"email\": \"manhetan@gmail.com\", \"firstName\": \"Vadim\", \"lastName\": \"Manannikov\", \"password\": \"qwerty1234\"}";
		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("manhetan@gmail.com"));
	}

	@Test
	@Order(2)
	void testGetAllUsers() throws Exception {
		mockMvc.perform(get("/api/users/1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(3)
	void testGetUserById() throws Exception {
		mockMvc.perform(get("/api/users/1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(4)
	void testUpdateUser() throws Exception {
		String userJson = "{\"email\": \"google@gmail.com\", \"password\": \"Qwert$4\"}";
		mockMvc.perform(put("/api/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(5)
	void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/users/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
}
