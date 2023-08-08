package sanko.sankons.domain.follow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sanko.sankons.domain.user.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	Follow findOneByFollowerAndFollowing(User follower, User following);
	List<Follow> findAllByFollower(User follower);
	List<Follow> findAllByFollowing(User following);

}
