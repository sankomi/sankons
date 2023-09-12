package sanko.sankons.web.dto;

import lombok.*; //Getter, Builder

@Getter
public class UserCheckFollowRequest {

	private Long user;

	@Builder
	public UserCheckFollowRequest(Long user) {
		this.user = user;
	}

}
