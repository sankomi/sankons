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

	private static Long userIds = 0L;
	private static Long followIds = 0L;

	private User createUser(String username) {
		User user = User.builder()
			.username(username)
			.password("password")
			.build();
		setField(user, "id", userIds++);

		return user;
	}

	@Test
	public void testFollowUser() throws Exception {
		//given
		User follower = createUser("follower");
		User following = createUser("following");

		SessionUser sessionFollower = new SessionUser(follower);

		when(userRepository.findById(follower.getId()))
			.thenReturn(Optional.of(follower));
		when(userRepository.findById(following.getId()))
			.thenReturn(Optional.of(following));

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

}
