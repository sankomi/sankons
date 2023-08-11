package sanko.sankons.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.*; //User, UserRepository
import sanko.sankons.domain.follow.*; //Follow, FollowRepository
import sanko.sankons.web.dto.SessionUser;

@RequiredArgsConstructor
@Service
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final SessionService sessionService;

	public boolean follow(Long userId, SessionUser sessionUser) throws Exception {
		if (sessionUser == null) throw new Exception("Not logged in");

		User follower = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new Exception("Invalid user"));
		User following = userRepository.findById(userId)
			.orElseThrow(() -> new Exception("Invalid user"));

		Follow follow = followRepository.save(Follow.builder()
			.follower(follower)
			.following(following)
			.build());

		return follow != null;
	}

}
