package sanko.sankons.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.follow.*; //Follow, FollowRepository
import sanko.sankons.web.dto.*; //SessionUser, UserFollowRequest

@RequiredArgsConstructor
@Service
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final SessionService sessionService;

	public boolean follow(UserFollowRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = findUser(sessionUser.getId());
		User following = findUser(request.getUser());

		Follow follow = followRepository.save(Follow.builder()
			.follower(follower)
			.following(following)
			.build());

		return follow != null;
	}

	public boolean checkFollow(Long userId, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = findUser(sessionUser.getId());
		User following = findUser(userId);

		Follow follow = followRepository.findOneByFollowerAndFollowing(follower, following);

		return follow != null;
	}

	public boolean unfollow(Long userId, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = findUser(sessionUser.getId());
		User following = findUser(userId);

		Follow follow = followRepository.findOneByFollowerAndFollowing(follower, following);

		if (follow == null) throw new Exception ("Not following");

		followRepository.delete(follow);

		return true;
	}

	private User findUser(Long userId) throws Exception {
		return userRepository.findById(userId)
			.orElseThrow(() -> new Exception("Invalid user"));
	}

}
