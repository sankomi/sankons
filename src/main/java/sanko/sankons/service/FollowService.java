package sanko.sankons.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.follow.*; //Follow, FollowRepository
import sanko.sankons.web.dto.*; //SessionUser, UserFollowRequest, UserCheckFollowRequest, UserFollowingResponse, UserFollowerResponse

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

	public boolean checkFollow(UserCheckFollowRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = findUser(sessionUser.getId());
		User following = findUser(request.getUser());

		Follow follow = followRepository.findOneByFollowerAndFollowing(follower, following);

		return follow != null;
	}

	public boolean unfollow(UserFollowRequest request, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = findUser(sessionUser.getId());
		User following = findUser(request.getUser());

		Follow follow = followRepository.findOneByFollowerAndFollowing(follower, following);

		if (follow == null) throw new Exception ("Not following");

		followRepository.delete(follow);

		return true;
	}

	private User findUser(Long userId) throws Exception {
		return userRepository.findById(userId)
			.orElseThrow(() -> new Exception("Invalid user"));
	}

	public UserFollowingResponse getFollowings(UserCheckFollowRequest request, SessionUser sessionUser) throws Exception {
		Long userId = request.getUser();

		if (userId == 0L) {
			if (sessionUser == null) throw new Exception("Not logged in");
			userId = sessionUser.getId();
		}

		User follower = findUser(userId);

		List<Follow> follows = followRepository.findAllByFollower(follower);

		if (follows == null) throw new Exception("No followers");

		return new UserFollowingResponse(follows);
	}

	public UserFollowerResponse getFollowers(UserCheckFollowRequest request, SessionUser sessionUser) throws Exception {
		Long userId = request.getUser();

		if (userId == 0L) {
			if (sessionUser == null) throw new Exception("Not logged in");
			userId = sessionUser.getId();
		}

		User following = findUser(userId);

		List<Follow> follows = followRepository.findAllByFollowing(following);

		if (follows == null) throw new Exception("No followings");

		return new UserFollowerResponse(follows);
	}

}
