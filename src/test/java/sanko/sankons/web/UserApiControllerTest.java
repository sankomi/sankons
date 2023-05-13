package sanko.sankons.web;

import org.junit.jupiter.api.*; //Test, BeforeAll
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import sanko.sankons.domain.user.User;
import sanko.sankons.service.UserService;
import sanko.sankons.web.dto.UserCreateRequest;
import sanko.sankons.web.dto.UserLoginRequest;

@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private static final String username = "username";
	private static final String password = "password";

	private static User user;
	private static ObjectMapper objectMapper;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();

		objectMapper = new ObjectMapper();
	}

	private static byte[] bytify(Object object) throws Exception {
		return objectMapper.writeValueAsBytes(object);
	}

	private ResultActions mockPost(String url, Object body) throws Exception {
		return mockMvc.perform(
			post(url)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(bytify(body))
		);
	}

	@Test
	public void testUserCreate() throws Exception {
		when(userService.create(any(UserCreateRequest.class)))
			.thenReturn(user);

		String url = "/api/v1/user/create";

		UserCreateRequest request = UserCreateRequest.builder()
			.username(username)
			.password(password)
			.build();
		mockPost(url, request).andExpect(status().isOk());

		UserCreateRequest emptyUsername = UserCreateRequest.builder()
			.username("  ")
			.password(password)
			.build();
		mockPost(url, emptyUsername).andExpect(status().isBadRequest());

		UserCreateRequest blankPassword = UserCreateRequest.builder()
			.username(username)
			.password("")
			.build();
		mockPost(url, blankPassword).andExpect(status().isBadRequest());
	}

	@Test
	public void testUserCheckLogin() throws Exception {
		when(userService.checkLogin()).thenReturn(username);

		mockMvc.perform(get("/api/v1/user/login"))
			.andExpect(status().isOk())
			.andExpect(content().string(username));
	}

	@Test
	public void testUserLogin() throws Exception {
		when(userService.login(any(UserLoginRequest.class)))
			.thenAnswer(invocation -> {
				UserLoginRequest request = invocation.getArgument(0, UserLoginRequest.class);
				if (!request.getUsername().equals(username)) return false;
				if (!user.checkPassword(request.getPassword())) return false;
				return true;
			});

		String url = "/api/v1/user/login";

		UserLoginRequest request = UserLoginRequest.builder()
			.username(username)
			.password(password)
			.build();
		mockPost(url, request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));

		UserLoginRequest wrongPassword = UserLoginRequest.builder()
			.username(username)
			.password("narpassword")
			.build();
		mockPost(url, wrongPassword)
			.andExpect(status().isOk())
			.andExpect(content().string("false"));

		UserLoginRequest emptyUsername = UserLoginRequest.builder()
			.username("  ")
			.password(password)
			.build();
		mockPost(url, emptyUsername).andExpect(status().isBadRequest());

		UserLoginRequest blankPassword = UserLoginRequest.builder()
			.username(username)
			.password("")
			.build();
		mockPost(url, blankPassword).andExpect(status().isBadRequest());
	}

	@Test
	public void testLogout() throws Exception {
		when(userService.logout()).thenReturn(true);

		mockMvc.perform(delete("/api/v1/user/login"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

}
