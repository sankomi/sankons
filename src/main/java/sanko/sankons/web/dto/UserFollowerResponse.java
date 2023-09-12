package sanko.sankons.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.follow.Follow;

@Getter
public class UserFollowerResponse {

	private List<UserInfoResponse> followers;

	public UserFollowerResponse(List<Follow> follows) {
		this.followers = follows.stream()
			.map(follow -> follow.getFollower())
			.map(UserInfoResponse::new)
			.collect(Collectors.toList());
	}

}
