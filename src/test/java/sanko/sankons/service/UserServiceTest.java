package sanko.sankons.service;

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.service.SessionService;
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest, UserChangePasswordRequest, UserChangeNameRequest, SessionUser

@ExtendWith(SpringExtension.class)
@Import(UserService.class)
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private SessionService sessionService;

	private static final Long id = 1L;
	private static final String username = "username";
	private static final String password = "password";

	private static String oldPassword = password;
	private static String newPassword = "new password";
	private static String confirmPassword = "new password";


	private static User user;

	@BeforeAll
	public static void beforeAll() {
		user = User.builder()
			.username(username)
			.password(password)
			.build();
	}

	@BeforeEach
	public void beforeEach() {
		when(userRepository.save(any(User.class)))
			.thenAnswer(invocation -> {
				ReflectionTestUtils.setField(user, "id", id);
				return user;
			});

		when(userRepository.findFirstByUsername(username))
			.thenReturn(user);
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
	public void testUserChangePassword() throws Exception {
		//given
		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(sessionUser);

		//when
		boolean changed = userService.changePassword(request, sessionUser);

		//then
		assertTrue(changed);
	}

	@Test
	public void testUserChangePasswordNoLogin() throws Exception {
		//given
		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> userService.changePassword(request, null));
		assertTrue(exception.getMessage().contains("Not logged in"));
	}

	@Test
	public void testUserChangePasswordNoConfirm() throws Exception {
		//given
		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword("different password")
			.build();

		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(null);

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> userService.changePassword(request, sessionUser));
		assertTrue(exception.getMessage().contains("Confirm password does not match"));
	}

	@Test
	public void testUserChangePasswordNoUser() throws Exception {
		//given
		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.ofNullable(null));

		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(sessionUser);

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> userService.changePassword(request, sessionUser));
		assertTrue(exception.getMessage().contains("Invalid user"));
	}

	@Test
	public void testUserChangePasswordIncorrectPassword() throws Exception {
		//given
		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword("incorrect password")
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(sessionUser);

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> userService.changePassword(request, sessionUser));
		assertTrue(exception.getMessage().contains("Incorrect password"));
	}

	@Test
	public void testUserLogin() {
		//given
		User user = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(user, "id", id);

		when(userRepository.findFirstByUsername(username))
			.thenReturn(user);

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
		//given
		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(new SessionUser(user));

		//when
		String login = userService.checkLogin(sessionUser);

		//then
		assertEquals(username, login);
	}

	@Test
	public void testUserCheckNoLogin() {
		//given
		SessionUser sessionUser = null;

		//when
		String login = userService.checkLogin(sessionUser);

		//then
		assertEquals(null, login);
	}

	@Test
	public void testUserChangeName() throws Exception {
		//given
		String newUsername = "new username";
		UserChangeNameRequest request = UserChangeNameRequest.builder()
			.username(newUsername)
			.build();

		ReflectionTestUtils.setField(user, "id", id);
		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		SessionUser sessionUser = new SessionUser(user);
		when(sessionService.getUser())
			.thenReturn(new SessionUser(user));

		//when
		boolean changed = userService.changeUsername(request, sessionUser);

		//then
		assertTrue(changed);
	}

	@Test
	public void testUserChangeNameNoLogin() throws Exception {
		//given
		String newUsername = "new username";
		UserChangeNameRequest request = UserChangeNameRequest.builder()
			.username(newUsername)
			.build();

		ReflectionTestUtils.setField(user, "id", id);
		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		SessionUser sessionUser = null;

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> userService.changeUsername(request, sessionUser));
		assertTrue(exception.getMessage().contains("Not logged in"));
	}

}
