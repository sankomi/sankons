package sanko.sankons.domain.follow;

import java.util.*; //Arrays, List

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.*; //Test, AfterEach

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sanko.sankons.domain.user.*; //User, UserRepository

@DataJpaTest
public class FollowRepositoryTest {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void after() {
		userRepository.deleteAll();
		followRepository.deleteAll();
	}

	private User addUser(String username) {
		return userRepository.save(User.builder()
			.username(username)
			.password("password")
			.build());
	}

	private Follow newFollow(User follower, User following) {
		return Follow.builder()
			.follower(follower)
			.following(following)
			.build();
	}

	@Test
	public void testFollow() {
		//given
		String followerName = "follower";
		String followingName = "following";

		User follower = addUser(followerName);
		User following = addUser(followingName);

		//when
		followRepository.save(newFollow(follower, following));

		//then
		Follow follow = followRepository.findOneByFollowerAndFollowing(follower, following);
		assertTrue(follow != null);
		assertEquals(followerName, follow.getFollower().getUsername());
		assertEquals(followingName, follow.getFollowing().getUsername());
	}

	@Test
	public void testFollowers() {
		//given
		String followerName = "follower";
		String followerTwoName = "followertwo";
		String followingName = "following";

		User follower = addUser(followerName);
		User followerTwo = addUser(followerTwoName);
		User following = addUser(followingName);

		//when
		followRepository.saveAll(Arrays.asList(
			newFollow(follower, following),
			newFollow(followerTwo, following)
		));

		//then
		List<Follow> follows = followRepository.findAllByFollowing(following);
		assertTrue(follows.size() == 2);
		for (Follow follow : follows) {
			assertTrue(follow.getFollowing().getUsername().equals(followingName));
		}
		assertTrue(follows.stream()
			.anyMatch(follow -> followerName.equals(follow.getFollower().getUsername())));
		assertTrue(follows.stream()
			.anyMatch(follow -> followerTwoName.equals(follow.getFollower().getUsername())));
	}

	@Test
	public void testFollowings() {
		//given
		String followerName = "follower";
		String followingName = "following";
		String followingTwoName = "followingtwo";

		User follower = addUser(followerName);
		User following = addUser(followingName);
		User followingTwo = addUser(followingTwoName);

		//when
		followRepository.saveAll(Arrays.asList(
			newFollow(follower, following),
			newFollow(follower, followingTwo)
		));

		//then
		List<Follow> follows = followRepository.findAllByFollower(follower);
		assertTrue(follows.size() == 2);
		for (Follow follow : follows) {
			assertTrue(follow.getFollower().getUsername().equals(followerName));
		}
		assertTrue(follows.stream()
			.anyMatch(follow -> followingName.equals(follow.getFollowing().getUsername())));
		assertTrue(follows.stream()
			.anyMatch(follow -> followingTwoName.equals(follow.getFollowing().getUsername())));
	}

}
