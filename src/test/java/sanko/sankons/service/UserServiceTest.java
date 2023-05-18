package sanko.sankons.service;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.web.dto.SessionUser;
import sanko.sankons.web.dto.UserCreateRequest;
import sanko.sankons.web.dto.UserLoginRequest;

@ExtendWith(SpringExtension.class)
@Import(UserService.class)
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private HttpSession httpSession;

	private static final Long id = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static User user;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();
	}

	@BeforeEach
	public void mockUserRepository() {
		when(userRepository.save(any(User.class)))
			.thenAnswer(invocation -> {
				ReflectionTestUtils.setField(user, "id", id);
				return user;
			});

		when(userRepository.findFirstByUsername(username))
			.thenReturn(user);

		when(httpSession.getAttribute("user"))
			.thenReturn(new SessionUser(user));
	}

	@Test
	public void testUserCreate() {
		//given
		UserCreateRequest request = UserCreateRequest.builder()
			.username(username)
			.password(password)
			.build();

		//when
		Long create = userService.create(request);

		//then
		assertEquals(id, create);
	}

	@Test
	public void testUserLogin() {
		//given
		UserLoginRequest request = UserLoginRequest.builder()
			.username(username)
			.password(password)
			.build();

		//when
		Boolean login = userService.login(request);

		//then
		assertEquals(true, login);
	}

	@Test
	public void testUserCheckLogin() {
		//when
		String login = userService.checkLogin();

		//then
		assertEquals(username, login);
	}

}
