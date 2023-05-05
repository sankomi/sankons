package sanko.sankons.web.dto;

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.user.User;

@Getter
@NoArgsConstructor
public class UserLoginRequest {

	private String username;
	private String password;

	@Builder
	public UserLoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
