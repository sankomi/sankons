package sanko.sankons.service;

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sanko.sankons.domain.follow.*; //Follow, FollowRepository
import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.web.dto.SessionUser;

@ExtendWith(SpringExtension.class)
@Import(FollowService.class)
public class FollowServiceTest {

	@Autowired
	private FollowService followService;

	@MockBean
	private FollowRepository followRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private SessionService sessionService;

	private static Long userIds = 1L;
	private static Long followIds = 1L;

	private User createUser(String username) {
		User user = User.builder()
			.username(username)
			.password("password")
			.build();
		setField(user, "id", userIds++);

		when(userRepository.findById(user.getId()))
			.thenReturn(Optional.of(user));

		return user;
	}

	@Test
	public void testFollowUser() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);

		when(followRepository.save(any(Follow.class)))
			.thenAnswer(invocation -> {
				Follow follow = (Follow) invocation.getArguments()[0];
				setField(follow, "id", followIds++);
				return follow;
			});

		//when
		boolean followed = followService.follow(following.getId(), sessionFollower);

		//then
		assertTrue(followed);
	}

	@Test
	public void testCheckFollowing() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);

		Follow follow = Follow.builder()
			.follower(follower)
			.following(following)
			.build();
		setField(follow, "id", followIds++);

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(follow);

		//when
		boolean check = followService.checkFollow(following.getId(), sessionFollower);

		//then
		assertTrue(check);
	}

	@Test
	public void testCheckFollowingNotFollowing() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(null);

		//when
		boolean check = followService.checkFollow(following.getId(), sessionFollower);

		//then
		assertFalse(check);
	}

	@Test
	public void testCheckFollowingNoFollowing() throws Exception {
		//given
		User follower = createUser("follower");

		SessionUser sessionFollower = new SessionUser(follower);

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> followService.checkFollow(0L, sessionFollower));
		assertTrue(exception.getMessage().contains("Invalid user"));
	}

}
