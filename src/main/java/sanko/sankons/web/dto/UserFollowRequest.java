package sanko.sankons.web.dto;

import jakarta.validation.constraints.NotNull;

import lombok.*; //Getter, NoArgsConstructor, Builder

@Getter
@NoArgsConstructor
public class UserFollowRequest {

	@NotNull(message = "User can't be null")
	private Long user;

	@Builder
	public UserFollowRequest(Long user) {
		this.user = user;
	}

}
