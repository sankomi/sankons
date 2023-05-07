package sanko.sankons.web.dto;

import jakarta.validation.constraints.*; //NotBlank, NotEmpty

import lombok.*; //Getter, NoArgsConstructor, Builder

import sanko.sankons.domain.user.User;

@Getter
@NoArgsConstructor
public class UserCreateRequest {

	@NotBlank(message = "Username can't be blank")
	private String username;

	@NotEmpty(message = "Password can't be empty")
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
