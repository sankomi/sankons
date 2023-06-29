package sanko.sankons.service;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.*; //Test, BeforeAll
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import sanko.sankons.domain.user.User;
import sanko.sankons.service.SessionService;
import sanko.sankons.web.dto.SessionUser;

@ExtendWith(SpringExtension.class)
@Import(SessionService.class)
public class SessionServiceTest {

	@Autowired
	private SessionService sessionService;

	@MockBean
	private HttpSession httpSession;

	private static final Long userId = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static User user;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
	}

	@Test
	public void testGetUser() {
		//given
		when(httpSession.getAttribute("user"))
			.thenReturn(new SessionUser(user));

		//when
		SessionUser sessionUser = sessionService.getUser();

		//then
		assertEquals(userId, sessionUser.getId());
		assertEquals(username, sessionUser.getUsername());
	}

	@Test
	public void testGetNullUser() {
		//given
		when(httpSession.getAttribute("user"))
			.thenReturn(null);

		//when
		SessionUser sessionUser = sessionService.getUser();

		//then
		assertEquals(null, sessionUser);
	}

	@Test
	public void testSetUser() {
		//given
		SessionUser sessionUser = new SessionUser(user);

		//whethen
		assertDoesNotThrow(() -> sessionService.setUser(sessionUser));
	}

	@Test
	public void testRemoveUser() {
		//whenthen
		assertDoesNotThrow(() -> sessionService.removeUser());
	}

}
