package sanko.sankons.service;

import java.util.*; //Optional, List, ArrayList

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
import sanko.sankons.web.dto.*; //SessionUser, UserFollowRequest, UserFollowingRequest, UserFollowerRequest

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

	private Follow createFollow(User follower, User following) {
		Follow follow = Follow.builder()
			.follower(follower)
			.following(following)
			.build();
		setField(follow, "id", followIds++);

		return follow;
	}

	@Test
	public void testFollowUser() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);
		UserFollowRequest request = UserFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followRepository.save(any(Follow.class)))
			.thenAnswer(invocation -> {
				Follow follow = (Follow) invocation.getArguments()[0];
				setField(follow, "id", followIds++);
				return follow;
			});

		//when
		boolean followed = followService.follow(request, sessionFollower);

		//then
		assertTrue(followed);
	}

	@Test
	public void testUnfollow() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);
		UserFollowRequest request = UserFollowRequest.builder()
			.user(following.getId())
			.build();
		Follow follow = createFollow(follower, following);

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(follow);

		//when
		boolean unfollowed = followService.unfollow(request, sessionFollower);

		//then
		assertTrue(unfollowed);
	}

	@Test
	public void testUnfollowNotFollowing() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);
		UserFollowRequest request = UserFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(null);

		//when
		Exception exception = assertThrows(Exception.class, () -> followService.unfollow(request, sessionFollower));
		assertTrue(exception.getMessage().contains("Not following"));
	}

	@Test
	public void testCheckFollowing() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);
		Follow follow = createFollow(follower, following);

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(follow);

		//when
		boolean check = followService.checkFollow(request, sessionFollower);

		//then
		assertTrue(check);
	}

	@Test
	public void testCheckFollowingNotFollowing() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followRepository.findOneByFollowerAndFollowing(follower, following))
			.thenReturn(null);

		//when
		boolean check = followService.checkFollow(request, sessionFollower);

		//then
		assertFalse(check);
	}

	@Test
	public void testCheckFollowingNoFollowing() throws Exception {
		//given
		User follower = createUser("follower");

		SessionUser sessionFollower = new SessionUser(follower);

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(0L)
			.build();

		//whenthen
		Exception exception = assertThrows(Exception.class, () -> followService.checkFollow(request, sessionFollower));
		assertTrue(exception.getMessage().contains("Invalid user"));
	}

	@Test
	public void testGetFollowing() throws Exception {
		//given
		User follower = createUser("follower");

		int length = (int) (Math.random() * 10 + 1);

		List<User> followings = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User following = createUser("following" + String.valueOf(i));
			Follow follow = createFollow(follower, following);
			followings.add(following);
			follows.add(follow);
		}

		SessionUser sessionFollower = new SessionUser(follower);

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(follower.getId())
			.build();

		when(followRepository.findAllByFollower(any(User.class)))
			.thenReturn(follows);

		//when
		UserFollowingResponse response = followService.getFollowings(request, sessionFollower);

		//then
		assertEquals(length, response.getFollowings().size());
	}

	@Test
	public void testGetFollower() throws Exception {
		//given
		User following = createUser("following");

		int length = (int) (Math.random() * 10 + 1);

		List<User> followers = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User follower = createUser("follower" + String.valueOf(i));
			Follow follow = createFollow(follower, following);
			followers.add(follower);
			follows.add(follow);
		}

		SessionUser sessionFollowing = new SessionUser(following);

		UserCheckFollowRequest request = UserCheckFollowRequest.builder()
			.user(following.getId())
			.build();

		when(followRepository.findAllByFollowing(any(User.class)))
			.thenReturn(follows);

		//when
		UserFollowerResponse response = followService.getFollowers(request, sessionFollowing);

		//then
		assertEquals(length, response.getFollowers().size());
	}

}
