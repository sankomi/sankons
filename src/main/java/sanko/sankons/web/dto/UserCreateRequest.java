package sanko.sankons.web.dto;

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.user.User;

@Getter
@NoArgsConstructor
public class UserCreateRequest {

	private String username;
	private String password;

	@Builder
	public UserCreateRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User toEntity() {
		return User.builder()
			.username(username)
			.password(password)
			.build();
	}

}
