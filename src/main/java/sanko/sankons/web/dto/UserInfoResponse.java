package sanko.sankons.web.dto;

import lombok.Getter;

import sanko.sankons.domain.user.User;

@Getter
public class UserInfoResponse {

	private Long id;
	private String username;

	public UserInfoResponse(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
	}

	public UserInfoResponse(SessionUser sessionUser) {
		this.id = sessionUser.getId();
		this.username = sessionUser.getUsername();
	}

}
