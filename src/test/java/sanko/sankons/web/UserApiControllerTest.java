package sanko.sankons.web;

import java.util.*; //List, ArrayList

import org.junit.jupiter.api.*; //Test, BeforeAll, BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.follow.Follow;
import sanko.sankons.service.*; //UserService, SessionService, FollowService
import sanko.sankons.web.dto.*; //UserCreateRequest, UserLoginRequest, UserChangePasswordRequest, UserChangeNameRequest, UserFollowRequest, UserCheckFollowRequest, UserFollowingResponse, UserFollowerResponse, SessionUser

@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private SessionService sessionService;

	@MockBean
	private FollowService followService;

	private static final String createUrl = "/api/v1/user/create";
	private static final String loginUrl = "/api/v1/user/login";
	private static final String changePasswordUrl = "/api/v1/user/password";
	private static final String changeUsernameUrl = "/api/v1/user/username";

	private static final Long id = 1L;
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
		ReflectionTestUtils.setField(user, "id", id);

		objectMapper = new ObjectMapper();
	}

	@BeforeEach
	public void mockUserService() throws Exception {
		when(userService.create(any(UserCreateRequest.class)))
			.thenReturn(id);

		when(userService.checkLogin(any(SessionUser.class))).thenReturn(username);

		when(userService.login(any(UserLoginRequest.class)))
			.thenAnswer(invocation -> {
				UserLoginRequest request = invocation.getArgument(0, UserLoginRequest.class);
				if (!request.getUsername().equals(username)) return false;
				if (!user.checkPassword(request.getPassword())) return false;
				return true;
			});

		when(userService.logout()).thenReturn(true);

		when(sessionService.getUser())
			.thenReturn(new SessionUser(user));
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

	private ResultActions mockPut(String url, Object body) throws Exception {
		return mockMvc.perform(
			put(url)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(bytify(body))
		);
	}

	private ResultActions mockDelete(String url, Object body) throws Exception {
		return mockMvc.perform(
			delete(url)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(bytify(body))
		);
	}

	private Long userIds = 151L;

	private User createUser(String username) {
		return createUser(username, "password");
	}

	private User createUser(String username, String password) {
		User user = User.builder()
			.username(username)
			.password(password)
			.build();
		ReflectionTestUtils.setField(user, "id", userIds++);

		return user;
	}

	@Test
	public void testUserCreate() throws Exception {
		UserCreateRequest request = UserCreateRequest.builder()
			.username(username)
			.password(password)
			.build();

		mockPost(createUrl, request)
			.andExpect(status().isOk())
			.andExpect(content().string(id.toString()));
	}

	@Test
	public void testUserCreateEmptyUsername() throws Exception {
		UserCreateRequest emptyUsername = UserCreateRequest.builder()
			.username("  ")
			.password(password)
			.build();

		mockPost(createUrl, emptyUsername).andExpect(status().isBadRequest());
	}

	@Test
	public void testUserCreateBlankPassword() throws Exception {
		UserCreateRequest blankPassword = UserCreateRequest.builder()
			.username(username)
			.password("")
			.build();

		mockPost(createUrl, blankPassword).andExpect(status().isBadRequest());
	}

	@Test
	public void testUserChangePassword() throws Exception {
		String oldPassword = password;
		String newPassword = "new password";
		String confirmPassword = "new password";

		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		when(userService.changePassword(any(UserChangePasswordRequest.class), any(SessionUser.class)))
			.thenReturn(true);

		mockPut(changePasswordUrl, request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testChangePasswordNoConfirm() throws Exception {
		String oldPassword = password;
		String newPassword = "new password";
		String confirmPassword = "different password";

		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		mockPost(changePasswordUrl, request).andExpect(status().isInternalServerError());
	}

	@Test
	public void testChangePasswordIncorrectPassword() throws Exception {
		String oldPassword = "incorrect password";
		String newPassword = "new password";
		String confirmPassword = "new password";

		UserChangePasswordRequest request = UserChangePasswordRequest.builder()
			.oldPassword(oldPassword)
			.newPassword(newPassword)
			.confirmPassword(confirmPassword)
			.build();

		mockPost(changePasswordUrl, request).andExpect(status().isInternalServerError());
	}

	@Test
	public void testUserCheckLogin() throws Exception {
		mockMvc.perform(get(loginUrl))
			.andExpect(status().isOk())
			.andExpect(content().string(username));
	}

	@Test
	public void testUserLogin() throws Exception {
		UserLoginRequest request = UserLoginRequest.builder()
			.username(username)
			.password(password)
			.build();

		mockPost(loginUrl, request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test void testUserLoginWrongPassword() throws Exception {
		UserLoginRequest wrongPassword = UserLoginRequest.builder()
			.username(username)
			.password("narpassword")
			.build();

		mockPost(loginUrl, wrongPassword)
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}

	@Test void testUserLoginEmptyUsername() throws Exception {
		UserLoginRequest emptyUsername = UserLoginRequest.builder()
			.username("  ")
			.password(password)
			.build();

		mockPost(loginUrl, emptyUsername).andExpect(status().isBadRequest());
	}

	@Test void testUserLoginBlankPassword() throws Exception {
		UserLoginRequest blankPassword = UserLoginRequest.builder()
			.username(username)
			.password("")
			.build();

		mockPost(loginUrl, blankPassword).andExpect(status().isBadRequest());
	}

	@Test
	public void testLogout() throws Exception {
		mockMvc.perform(delete("/api/v1/user/login"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testUserChangeName() throws Exception {
		String newUsername = "new username";

		UserChangeNameRequest request = UserChangeNameRequest.builder()
			.username(newUsername)
			.build();

		when(userService.changeUsername(any(UserChangeNameRequest.class), any(SessionUser.class)))
			.thenReturn(true);

		mockPut(changeUsernameUrl, request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testUserChangeNameBlankUsername() throws Exception {
		String blankUsername = " ";

		UserChangeNameRequest request = UserChangeNameRequest.builder()
			.username(blankUsername)
			.build();

		mockPut(changeUsernameUrl, request)
			.andExpect(status().isBadRequest());
	}

	@Test
	public void testUserFollow() throws Exception {
		User following = createUser("following");

		UserFollowRequest request = UserFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followService.follow(any(UserFollowRequest.class), any(SessionUser.class)))
			.thenReturn(true);

		mockPut("/api/v1/user/follow", request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testUserUnfollow() throws Exception {
		User following = createUser("following");

		UserFollowRequest request = UserFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followService.unfollow(any(UserFollowRequest.class), any(SessionUser.class)))
			.thenReturn(true);

		mockDelete("/api/v1/user/follow", request)
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testUserCheckFollow() throws Exception {
		User following = createUser("following");

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followService.checkFollow(any(UserCheckFollowRequest.class), any(SessionUser.class)))
			.thenReturn(true);

		mockMvc.perform(get("/api/v1/user/follow?user=" + String.valueOf(following.getId())))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testGetFollowings() throws Exception {
		User follower = createUser("follwer");

		int length = (int) (Math.random() * 10 + 1);

		List<Follow> follows = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User following = createUser("following" + String.valueOf(i));
			Follow follow = Follow.builder()
				.follower(follower)
				.following(following)
				.build();
			follows.add(follow);
		}

		when(followService.getFollowings(any(UserCheckFollowRequest.class), any(SessionUser.class)))
			.thenReturn(new UserFollowingResponse(follows));

		mockMvc.perform(get("/api/v1/user/followings?user=" + String.valueOf(follower.getId())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.followings", hasSize(length)));
	}

	@Test
	public void testGetFollowers() throws Exception {
		User following = createUser("follwing");

		int length = (int) (Math.random() * 10 + 1);

		List<Follow> follows = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User follower = createUser("follower" + String.valueOf(i));
			Follow follow = Follow.builder()
				.follower(follower)
				.following(following)
				.build();
			follows.add(follow);
		}

		when(followService.getFollowers(any(UserCheckFollowRequest.class), any(SessionUser.class)))
			.thenReturn(new UserFollowerResponse(follows));

		mockMvc.perform(get("/api/v1/user/followers?user=" + String.valueOf(following.getId())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.followers", hasSize(length)));
	}

}
