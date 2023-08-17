package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.follow.Follow;

@Getter
public class UserFollowingResponse {

	private List<UserInfoResponse> followings;

	public UserFollowingResponse(List<Follow> follows) {
		this.followings = follows.stream()
			.map(follow -> follow.getFollowing())
			.map(UserInfoResponse::new)
			.collect(Collectors.toList());
	}

}
